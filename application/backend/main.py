from flask import Flask, request, jsonify
from flask_cors import CORS

import uuid
import redis
import pika
import waitress
import time
import logging

rabbit_address = "rabbit-db"
redis_address = "redis-db"
#rabbit_address = "10.2.4.134"
#redis_address = "10.2.4.179"


app = Flask("myCalculatrice")
cors = CORS(app, resources={ r"/api/*": {"origins": "*"}})

logging.root.setLevel(level=logging.INFO)

logging.info("Connexion à la base de données Redis")
redis_client = redis.Redis(host=redis_address, port=6379, db=0, decode_responses=True)

def connection_rabbit():
    logging.info("Tentative de connexion au RabbitMQ")
    connection = pika.BlockingConnection(pika.ConnectionParameters(rabbit_address))
    channel = connection.channel()
    logging.info("Déclaration de la queue")
    channel.queue_declare(queue='calcul')
    return channel

# Préfixe des routes
API_PREFIX = "/api/v1/"

@app.route(API_PREFIX+"/calculate", methods=["POST"])
def calculate():
    """
    Forme de data attendu : {"calcul": "a+b*c/d-e*f etc..."}
    """
    logging.info(f"Requête POST /calculate | IP : {request.remote_addr} | Data : {request.get_json()}")
    data = request.get_json()

    calcul = data.get("calcul")
    if not calcul:
        logging.info("Requête POST /calculatrice sans calcul")
        return jsonify({"error": "Invalid request. Provide 'calcul' as a string representing the operation."}), 400

        # Validation de l'expression pour éviter les caractères non autorisés

    allowed_chars = "0123456789+-*/(). "
    if any(char not in allowed_chars for char in calcul):
        return jsonify({"error": "Expression contains invalid characters."}), 400
    
    # Générer un ID unique pour le calcul
    operation_id = str(uuid.uuid4())

    try:
        logging.info("Envoi du message dans le canal")
        channel = connection_rabbit()
        channel.basic_publish(exchange='', routing_key='calcul', body=f"{operation_id}@{calcul}")
        logging.info(f"Message envoyé dans le canal : {operation_id}@{calcul}")
    except Exception as e:
        logging.error("Erreur lors de l'écriture dans le canal")
        return jsonify({"error": f"An error occurred while evaluating the expression: {str(e)}"}), 500

    return jsonify({"operation_id": operation_id}), 201


@app.route(API_PREFIX+"/result/<operation_id>", methods=["GET"])
def get_result(operation_id):
    #Renvoie le résultat de l'opération associé à l'operation_id

    logging.info(f"Récupération de l'opération {operation_id}")
    result = redis_client.get(operation_id)
    if result is None:
        logging.warning(f"Aucun calcul avec l'opération {operation_id} a été trovué")
        return jsonify({"error": "Operation ID not found."}), 404

    return jsonify({"operation_id": operation_id, "result": str(result)}), 200

if __name__ == "__main__":
    #app.run(host="0.0.0.0", port=5000, debug=True)
    waitress.serve(app, host="0.0.0.0", port=5000)
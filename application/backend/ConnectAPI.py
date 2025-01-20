from flask import Flask, request, jsonify
import uuid
import redis
import pika


app = Flask("myCalculatrice")
redis_client = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.queue_declare(queue='calcul')

# Préfixe des routes
API_PREFIX = "/api/v1/myCalculatrice"

@app.route(API_PREFIX+"/calculate", methods=["POST"])
def calculate():
    """
    Forme de data attendu : {"calcul": "a+b*c/d-e*f etc..."}
    """
    try: 
        connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
        channel = connection.channel()
        channel.queue_declare(queue='calcul')
    except Exception as e:
        return jsonify({"error": f"An error occurred while evaluating the expression: {str(e)}"}), 500

    data = request.get_json()

    calcul = data.get("calcul")
    if not calcul:
        return jsonify({"error": "Invalid request. Provide 'calcul' as a string representing the operation."}), 400

        # Validation de l'expression pour éviter les caractères non autorisés
    allowed_chars = "0123456789+-*/(). "
    if any(char not in allowed_chars for char in calcul):
        return jsonify({"error": "Expression contains invalid characters."}), 400



    # Générer un ID unique pour le calcul
    operation_id = str(uuid.uuid4())

    try:
        channel.basic_publish(exchange='', routing_key='calcul', body=calcul)
    except Exception as e:
        return jsonify({"error": f"An error occurred while evaluating the expression: {str(e)}"}), 500

    connection.close()

    return jsonify({"operation_id": operation_id}), 201


@app.route(API_PREFIX+"/result/<operation_id>", methods=["GET"])
def get_result(operation_id):
    #Renvoie le résultat de l'opération associé à l'operation_id

    result = redis_client.get(operation_id)
    if result is None:
        return jsonify({"error": "Operation ID not found."}), 404

    return jsonify({"operation_id": operation_id, "result": str(result)}), 200

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)

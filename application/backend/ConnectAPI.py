from flask import Flask, request, jsonify
import redis
import uuid


app = Flask("myCalculatrice")
redis_client = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)

# Préfixe des routes
API_PREFIX = "/api/v1/myCalculatrice"

@app.route(API_PREFIX+"/calculate", methods=["POST"])
def calculate():
    #Forme de data attendu: {"operation": "add/subtract/multiply/divide", "values": [a, b]}

    data = request.get_json()

    operation = data.get("operation")
    values = data.get("values")
    if not operation or not values or len(values) != 2:
        return jsonify({"error": "Invalid request. Provide 'operation' and 'values' with 2 numbers."}), 400

    a, b = values
    try:
        a, b = float(a), float(b)
    except ValueError:
        return jsonify({"error": "Values must be numbers."}), 400

    try:
        if operation == "add":
            result = a + b
        elif operation == "subtract":
            result = a - b
        elif operation == "multiply":
            result = a * b
        elif operation == "divide":
            if b == 0:
                return jsonify({"error": "Division by zero is not allowed."}), 400
            result = a / b
        else:
            return jsonify({"error": "Unsupported operation. Use 'add', 'subtract', 'multiply', or 'divide'."}), 400
    except Exception as e:
        return jsonify({"error": "An error occurred: "+str(e)}), 500

    operation_id = str(uuid.uuid4())

    # Stockage du résultat dans Redis
    redis_client.set(operation_id, result)

    return jsonify({"operation_id": operation_id}), 201

@app.route(API_PREFIX+"/result/<operation_id>", methods=["GET"])
def get_result(operation_id):
    #Renvoie le résultat de l'opération associé à l'operation_id

    result = redis_client.get(operation_id)
    if result is None:
        return jsonify({"error": "Operation ID not found."}), 404

    return jsonify({"operation_id": operation_id, "result": float(result)}), 200

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)

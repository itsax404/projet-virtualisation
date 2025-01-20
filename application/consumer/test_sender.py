import pika
import random
import time
from uuid import uuid4

# Establish a connection to RabbitMQ server
connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()

# Declare the queue
channel.queue_declare(queue='calcul')

# Function to generate random calculus elements
def generate_calculus_element():
    id = uuid4()
    operations = ['+', '-', '*', '/']
    num1 = random.randint(1, 100)
    num2 = random.randint(1, 100)
    operation = random.choice(operations)
    return f"{id}@{num1}{operation}{num2}"

# Send random calculus elements to the "calcul" channel
try:
    while True:
        calculus_element = generate_calculus_element()
        channel.basic_publish(exchange='', routing_key='calcul', body=calculus_element)
        print(f" [x] Sent '{calculus_element}'")
        time.sleep(1)  # Wait for 1 second before sending the next element
except KeyboardInterrupt:
    print(" [x] Stopped sending messages")

# Close the connection
connection.close()
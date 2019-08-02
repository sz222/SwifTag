from __future__ import print_function
from kafka import KafkaProducer, KafkaConsumer
from flask import Flask, render_template, request, session
from flask_socketio import SocketIO, emit
from threading import Thread
import sys
import json

#Configuration
app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app)
kafka_bs = ['10.0.0.8:9092','10.0.0.7:9092','10.0.0.11:9092']
k_in_topic = 'streams-questions-input'
k_out_topic = 'streams-tags-output'
k_producer = KafkaProducer(bootstrap_servers=kafka_bs)
k_consumer = KafkaConsumer(
    k_out_topic,
    bootstrap_servers=kafka_bs,
    value_deserializer=lambda x: json.loads(x)
)
consumer_thread = None

#Define background function on server side to get data from consumer topic
def background():
    print('Start background kafka message consuming')
    for msg in k_consumer:
        print('receive consumer message: ' + str(msg.value), file=sys.stderr)
        with app.test_request_context('/'):
            socketio.emit('tags', msg.value, room=msg.value.get("sid"))

if consumer_thread is None:
    consumer_thread = Thread(target=background)
    consumer_thread.setDaemon(True)
    consumer_thread.start()

#Define different response for different routes

#default template
@app.route('/')
def hello():
    return render_template('app.html')

#socket.io library attach user id for each message produced
@socketio.on('question')
def handle_message(message):
    print('received message: ' + str(message) + ' from ' + str(request.sid), file=sys.stderr)
    k_producer.send(k_in_topic, json.dumps(message))

#socket.io library connect client and identify message receiver based on session id: "request.sid"
@socketio.on('connect')
def handle_connection():
    print('client ' + str(request.sid) + ' connected', file=sys.stderr)
    emit('sid', request.sid, room=request.sid)


if __name__ == "__main__":
    socketio.run(app)
    app.run(host="0.0.0.0", debug=True) # port=80,
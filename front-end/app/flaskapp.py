from kafka import KafkaProducer, KafkaConsumer
from kafka import KafkaClient, SimpleConsumer
from flask import Flask, render_template, request, redirect, Response
import random, json
import time
import sys  

app = Flask(__name__)

kafkakserver = ['10.0.0.8:9092','10.0.0.7:9092','10.0.0.11:9092']
producer = KafkaProducer(bootstrap_servers=kafkakserver)
# ,
#                       topics=['newQuestion'],
#                       value_serializer=lambda x: 
#                       dumps(x))

##1. initially, when people want to ask question, show a website of question template
@app.route('/')
def initiate(): 
    return render_template('question.html')

##2. when people click "NEXT" button after entered question title and question body
## Flask worksas 

##Flask works as consumer when Kafka stream returns tags, when request is "tags"
# @app.route('/tags')
# def get_question_tag():
#     #consumer part
#     consumer = KafkaConsumer(boostrap_servers=kafkakserver)
#     consumer.subscribe(topics=['topTags'])

#     try:
#       for msg in consumer:
#         return render_template('tags.html')
#         #in the future render the website of tags recommendation: 
#     finally:
#       consumer.close()


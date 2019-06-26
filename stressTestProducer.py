# import base64
# import struct
import json
import re
import sys
import time
from multiprocessing import Process
# import config 
from kafka import KafkaProducer, KafkaConsumer
from threading import Thread
# from __future__ import print_function
# import smart_open
# import boto
import pandas as pd
# from utils import np_to_json
# import numpy as np
# import pickle
kafka_bs = ['10.0.0.8:9092','10.0.0.7:9092','10.0.0.11:9092'] #
k_in_topic = 'streams-questionStreamTest-input'
k_out_topic = 'streams-tagStreamTest-output'


k_producer = KafkaProducer(bootstrap_servers=kafka_bs)
consumer_thread = None

# data = pd.read_csv("s3://insightdeshuyan/streamData/all-000000000041.csv")
data = pd.read_csv("s3://insightdeshuyan/tags/questions_sample_2k.csv")
# print(data.head(5))
# consumer_thread = None
# start_time = time.time()
# print start_time
# for row in data.rows:
#    print row['c1'], row['c2']
df = pd.DataFrame(data)
df = df[['id', 'title','body']]
df = df[:1]
df.columns = ['id', 'title', 'content']
length = df.shape[0] #get number of rows
print("original dat size is : ******" + str(length))
# print('received message: ' + str(message) + ' from ' + str(request.sid), file=sys.stderr)

k_consumer = KafkaConsumer(
    k_in_topic,
    bootstrap_servers=kafka_bs
    # value_deserializer=lambda x: json.loads(x)
)

def background():
    print('Start background kafka message consuming')
    cnt = 0   
    for msg in k_consumer:
        cnt += 1
        print('receive consumer message: ' + str(msg.value))
        if cnt == length:
            print("totle number of message recived is: " + str(cnt))
        # with app.test_request_context('/'):
        #     socketio.emit('tags', msg.value, room=msg.value.get("sid"))
     
if consumer_thread is None:
    consumer_thread = Thread(target=background)
    consumer_thread.setDaemon(True)
    consumer_thread.start()
    time.sleep(3)

for record in df.to_dict(orient='records'): #df.iterrows()
    k_producer.send(k_in_topic, json.dumps(record))
    print(record)

k_producer.flush()
time.sleep(3)

# print("totle data size is: " + str(length))

# if __name__ == "__main__":
#     # socketio.run(app)
#     # app.run(host="0.0.0.0", debug=True)


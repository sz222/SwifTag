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
from s3fs import S3FileSystem
# from utils import np_to_json
# import numpy as np
# import pickle
kafka_bs = ['10.0.0.8:9092','10.0.0.7:9092','10.0.0.11:9092'] #
k_in_topic = 'streams-questions-input'
k_out_topic = 'streams-tags-output'


k_producer = KafkaProducer(bootstrap_servers=kafka_bs)
consumer_thread = None

# data = pd.read_csv("s3://insightdeshuyan/streamData/all-000000000041.csv")
data = pd.read_csv("s3://insightdeshuyan/tags/questions_sample_2k.csv")
df = pd.DataFrame(data)
df = df[['id', 'title','body']]
df.columns = ['id', 'title', 'content']
length = df.shape[0] #get number of rows
print("original dat size is : ******" + str(length))

k_consumer = KafkaConsumer(
    k_out_topic,
    bootstrap_servers=kafka_bs,
    value_deserializer=lambda x: json.loads(x)
)

def background():
    print('Start background kafka message consuming')
    cnt = 0   
    for msg in k_consumer:
        cnt += 1
        if cnt % 10 == 0:
            print("Received " + str(cnt) + " message")
        if cnt % 1000 == 0 && cnt < 10000:
            print("totle number of message recived is: " + str(cnt))
            consumer_end_time = time.time()
            consumer_seconds_elapsed = consumer_end_time - start_time
            print(consumer_seconds_elapsed)
            print("time to deal with each message:")
            print(consumer_seconds_elapsed / length)
     
if consumer_thread is None:
    consumer_thread = Thread(target=background)
    consumer_thread.setDaemon(True)
    consumer_thread.start()
    time.sleep(3)

producer_start_time = time.time()
for record in df.to_dict(orient='records'): #df.iterrows()
    print('new record')
    k_producer.send(k_in_topic, json.dumps(record))
producer_end_time = time.time()
producer_seconds_elapsed = producer_end_time - producer_start_time
print("time for produce: ")
print(producer_seconds_elapsed)
print("number of message per second:")
print(length / producer_seconds_elapsed)


k_producer.flush()
raw_input('Press Enter to exit')

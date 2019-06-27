# import base64
from __future__ import print_function
# import struct
import json
import re
import sys
import time
from multiprocessing import Process
# import config 
from kafka import KafkaProducer, KafkaConsumer
from threading import Thread

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

consumer_thread = None

k_consumer = KafkaConsumer(
    k_out_topic,
    bootstrap_servers=kafka_bs,
    value_deserializer=lambda x: json.loads(x)
)

def background():
    print('Start background kafka message consuming')
    cnt = 0
    time_sum = 0   
    for msg in k_consumer:
        cnt += 1
        consumer_end_time = time.time() #message recived time
        msg_send_time = float(msg.value.get('timestamp')) #message send time 
        consumer_seconds_elapsed = consumer_end_time - msg_send_time
        time_sum = time_sum + consumer_seconds_elapsed
        if cnt % 100 == 0 & cnt < 10000:
            print(msg_send_time)
            print(consumer_end_time)
            print(consumer_seconds_elapsed)
            print("totle number of message recived is: " + str(cnt))
            print("time to deal with each message:")
            print(time_sum / cnt)
     
# raw_input('Press Enter to exit')
if __name__ == '__main__':
    background()

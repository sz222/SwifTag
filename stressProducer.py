# import base64
# import struct
import json
import re
import sys
import time
from multiprocessing import Process
# import config 
from kafka import KafkaProducer
from threading import Thread
# from __future__ import print_function
# import smart_open
# import boto
import pandas as pd
from s3fs import S3FileSystem
# from utils import np_to_json
# import numpy as np
# import pickle

def main():
    
    kafka_bs = ['10.0.0.8:9092','10.0.0.7:9092','10.0.0.11:9092'] #
    k_in_topic = 'streams-questions-input'
    k_producer = KafkaProducer(bootstrap_servers=kafka_bs)

    # with open("s3://insightdeshuyan/tags/questions_sample_2k.csv", "r") as f:
    data = pd.read_csv("s3://insightdeshuyan/streamData/all-000000000041.csv")
    df = pd.DataFrame(data)
    df = df[['id', 'title','body']]
    df.columns = ['id', 'title', 'content']
    length = df.shape[0] #get number of rows

    

    print("original dat size is : ******" + str(length))
    producer_start_time = time.time()
    cnt = 0
    # while True:
    for record in df.to_dict(orient='records'): #df.iterrows()
        cnt += 1
        # print('new record')
        if cnt % 100 == 0 & cnt < 5000:
            print("number of message sent per second:" + str(cnt))
            seconds = time.time() - producer_start_time
            print("time from start is : " + str(seconds))
            print("msg per second:")
            print(cnt / seconds)
        send_time_each_msg = time.time()
        record.update( {'send_time' : send_time_each_msg} )  
        # print(record)
        k_producer.send(k_in_topic, json.dumps(record))
    producer_end_time = time.time()
    print(producer_end_time)
    producer_seconds_elapsed = producer_end_time - producer_start_time
    print("time for producer: ")
    print(producer_seconds_elapsed)
    print("number of message sent per second:")
    print(length / producer_seconds_elapsed)
    k_producer.flush()
    raw_input('Press Enter to exit')
        # f.close()
    return

if __name__ == '__main__':
    main()


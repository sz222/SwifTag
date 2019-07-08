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
import config 
from s3fs import S3FileSystem
from multiprocessing import Pool  #  multiprocessing.pool  ,ThreadPool as 
# from utils import np_to_json
# import numpy as np
# import pickle

# def main():
    
#     kafka_bs = ['10.0.0.8:9092','10.0.0.7:9092','10.0.0.11:9092'] #
#     k_in_topic = 'streams-questions-input'
#     k_producer = KafkaProducer(bootstrap_servers=kafka_bs
#                                 ,value_serializer=lambda value: json.dumps(value),batch_size=65536, linger_ms = 100)
#                                 # , linger_ms = 50)

    # with open("s3://insightdeshuyzan/tags/questions_sample_2k.csv", "r") as f:
    # data = pd.read_csv("s3://insightdeshuyan/tags/questions_sample_2k.csv")  #   s3://insightdeshuyan/streamData/all-000000000041.csv
    # df = pd.DataFrame(data)
    # df = df[['id', 'title','body']]
    # df.columns = ['id', 'title', 'content']
    # length = df.shape[0] #get number of rows

    
def single_producer(i):
    kafka_bs = ['10.0.0.8:9092','10.0.0.7:9092','10.0.0.11:9092'] #
    k_in_topic = 'streams-questions-input'
    k_producer = KafkaProducer(bootstrap_servers=kafka_bs,
                                value_serializer=lambda value: json.dumps(value)
                                    ,batch_size=655360
                                    ,compression_type='gzip')
    data_path = "s3://insightdeshuyan/tags/questions_sample_2k" + "_" + str(i) + ".csv"
                                # ,value_serializer=lambda value: json.dumps(value),batch_size=65536, linger_ms = 100)
    # read inzzz
    data = pd.read_csv(data_path)  #   s3://insightdeshuyan/streamData/all-000000000041.csv
    df = pd.DataFrame(data)
    df = df[['id', 'title','body']]
    df.columns = ['id', 'title', 'content']
    length = df.shape[0]
    print("original dat size is : ******" + str(length))

    producer_start_time = time.time()
    cnt = 0
    for record in df.to_dict(orient='records'): 
        cnt += 1
        if cnt % 100 == 0 & cnt < 5000:
            print("number of message sent per second:" + str(cnt))
            seconds = time.time() - producer_start_time
            print("time from start is : " + str(seconds))
            print("msg per second:")
            print(cnt / seconds)
        send_time_each_msg = time.time()  
        record.update( {'timestamp' : send_time_each_msg} ) 
        s = json.dumps(record) 
        k_producer.send(k_in_topic, s) ##, json.dumps(record)
    producer_end_time = time.time()
    print(producer_end_time)
    producer_seconds_elapsed = producer_end_time - producer_start_time
    print("time for producer: ")
    print(producer_seconds_elapsed)
    print("#####################")
    print("number of message sent per second for thread:" + str(i))
    print("#####################")
    print(length / producer_seconds_elapsed)
    # k_producer.flush()
        # f.close()
    return

# def test(i):
#     print("hello world" + str(i))

pool_size = 4
pool = Pool(pool_size)

for i in range(pool_size):
    pool.apply_async(single_producer, (i,))
pool.close()
pool.join()

raw_input('Press Enter to exit')



# if __name__ == '__main__':
#     main()


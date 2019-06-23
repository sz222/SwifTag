# Play with Kafka Stream Application Steps

This file is used to list steps for building Kafka Stream application that ingest Stackoverflow question topic and output topic of question title key words and find associated top 3 tags to recommend to the end users.
This tutorial assume you have already installed Kafka and Zookeeper from Confluent platform, if not, you can go to [Confluent official document](https://docs.confluent.io/current/installation/installing_cp/zip-tar.html#prod-kafka-cli-install) for installation info.
In addition, here's the [tutorial](https://kafka.apache.org/22/documentation/streams/tutorial) for how to write Kafka Stream application with Maven project.
### Solution: 
**Step 1: Start Kafka Server:**\
    Under Confluent home path, run the following commands:
* Start Zookeeper:
 ```
    > bin/zookeeper-server-start.sh config/zookeeper.properties
```
* Start Kafka:
 ```
    > bin/kafka-server-start.sh config/server.properties
```
**Step 2: Create input topic and output topic:**
* Input topic:
```
        > bin/kafka-topics.sh --create \
        --bootstrap-server localhost:9092 \
        --replication-factor 1 \
        --partitions 1 \
        --topic streams-plaintext-input
```
*    Output topic:
```
    > bin/kafka-topics.sh --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-wordcount-output \
    --config cleanup.policy=compact
 ```
**Step 3: Start KStream application and open producer:**
* Start demo application:
```
    > bin/kafka-run-class.sh org.apache.kafka.streams.examples.wordcount.WordCountDemo
```
* Open a producer in a new terminal:
```
    > bin/kafka-console-producer.sh --broker-list localhost:9092 --topic streams-plaintext-input
```
**Step 4: Process some data:**
*  Message entered into producer console will be processed into output
```
    > bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic streams-wordcount-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```
***Appendix: Confluent related commands***
Under Confluent folder:
* List available topic:
```
    ./bin/kafka-topics --zookeeper localhost:2181 --list
```
* Remove a topic
```
    ./bin/kafka-topics --zookeeper localhost:2181 --delete --topic streams-tagcount-output-1
```
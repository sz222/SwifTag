# Connect Kafka with Redis on Confluent

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)
This file is used for Kafka-Redis configuration information.
#
### Solution Steps
**Files:**\
    1. **Create Kafka Topic**:  
   ```sh
# config.storage.topic=connect-configs
  bin/kafka-topics --create --zookeeper localhost:2181 --topic connect-configs --replication-factor 3 --partitions 1 --config cleanup.policy=compact

# offset.storage.topic=connect-offsets
  bin/kafka-topics --create --zookeeper localhost:2181 --topic connect-offsets --replication-factor 3 --partitions 50 --config cleanup.policy=compact

# status.storage.topic=connect-status
  bin/kafka-topics --create --zookeeper localhost:2181 --topic connect-status --replication-factor 3 --partitions 10 --config cleanup.policy=compact
```
\
    2. **Configure woker-properties file**: 
    See [Confluent Documents](https://docs.confluent.io/current/connect/userguide.html)




  


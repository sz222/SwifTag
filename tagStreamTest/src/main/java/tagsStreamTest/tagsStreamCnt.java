package tagsStreamTest;
/*
This application file processes example question streamings and interacts with Redis and get count number of tags
 */
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class tagsStreamCnt {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
            props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
            props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();
        Jedis jedis = new Jedis("localhost", 6397);
        jedis.connect();
        //build source with String type key and String type value, key: keyword and value: tag
        KStream<String, String> source = builder.stream("streams-plaintext-input");

        source.mapValues(value -> {
            System.out.println(value);
            String tag = value;
            String count = jedis.get(tag);
            return tag + ":" + count;
        }).to("streams-tagcount-output-1");

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
            System.exit(0);
        }

}

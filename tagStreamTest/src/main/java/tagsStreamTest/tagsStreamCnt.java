package tagsStreamTest;
/*
This application file processes example question streamings and do aggregation on tags and interacts with Redis
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

        builder.<String, String>stream("streams-plaintext-input").to("streams-pipe-output");


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

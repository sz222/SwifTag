package questionStream;
/*
This application file processes question stream data and interacts with Redis and
provide tags recommendation for incoming questions
 */
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import redis.clients.jedis.Jedis;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class tagsStreamCnt {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
            props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.0.8:9092");
            props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
            props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();
        Jedis jedis = new Jedis("10.0.0.12", 6379);
        jedis.connect();
        //build source with String type key and String type value, key: keyword and value: tag
        KStream<String, String> source = builder.stream("streams-questions-input");

        //for each question title in the incoming streaming data, extract keywords, look up its tags count in redis
        //and come up with top 3 tags recommendation
        source.mapValues(value -> {
            Gson gson = new GsonBuilder().create();
            Question question = gson.fromJson(value, Question.class);
//            System.out.println(question.getTitle());
//            String[] tags = getTagsRecommended(question.getTitle(), jedis);
            getTagsRecommended(question.getTitle(), jedis);
//            question.setTags(tags);
//            question.setTags(question.getTitle().split(" "));
//            System.out.println(gson.toJson(question));
            return gson.toJson(question);

        }).to("streams-tags-output");

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

    public static void getTagsRecommended(String title, Jedis jedis) {
//        if (!stopWords.contains(word))
//        String[] tagsList = new String[3];

        //Schema of Values in Redis table:
        //HGETALL questionTag:partsums
        //1) "collect_list(tagTuple)"
        //2) "WrappedArray([scheme,1], [stream,1])"
        Map<String, Integer> tagMap = new HashMap<>(); //create a map with key:tag name value: tag count
        
        String[] keyWords = title.split(" ");
        for (String word : keyWords) {
            if (!stopWords.contains(word.toLowerCase())) {
                if (jedis.hgetAll("questionTag:" + word.toLowerCase()) != null) {
                    for(String key : jedis.hgetAll("questionTag:" + word.toLowerCase()).keySet()) {
                        System.out.println("*****" + key);
                    };
                    //iterate through the value(array of [tag, count] tuple of keyword in Redis
//                    String[] tagsCount = jedis.hgetAll("questionTag:" + word.toLowerCase());
//
                }
            }
        }
//        return tagsList;
    }

    public static Set<String> stopWords = new HashSet<String>(Arrays.asList(
            "and", "an", "i", "not", "is", "are", "?",
            ".", "who", "in", "to", "how", "a", "the", "using" ));
}

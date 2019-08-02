/*
This application file processes question stream data and interacts with Redis and
provide tags recommendation for incoming questions
 */
package questionStream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import redis.clients.jedis.Jedis;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class tagsStreamCnt {

    public static void main(String[] args) throws Exception {
        //configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.0.8:9092,10.0.0.7:9092,10.0.0.11:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 16);

        final StreamsBuilder builder = new StreamsBuilder();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(64);
        JedisPool jedisPool = new JedisPool(poolConfig, "10.0.0.12", 6379);


        //build source with String type key and String type value, key: keyword and value: tag
        KStream<String, String> source = builder.stream("streams-questions-input");

        //for each question title in the incoming streaming data, extract keywords, look up its tags count in redis
        //and come up with top 3 tags recommendation
        source.mapValues(value -> {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                Gson gson = new GsonBuilder().create();
                Question question = gson.fromJson(value, Question.class); //construct question class based on received json file messagex
                Set<String> tags = getTagsRecommended(question.getTitle(), jedis); //return top 3 recommended tags based on input question title
                question.setTags(tags.toArray(new String[0])); //set tag field for each question class object
                return gson.toJson(question);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }


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

    public static Set<String> getTagsRecommended(String title, Jedis jedis) {
        //Schema of Values stored in Redis table, table name: questionTag:
        //HGETALL questionTag:partsums
        //1) "collect_list(tagTuple)"
        //2) "WrappedArray([scheme,1], [stream,1])"
        Map<String, Integer> tagMap = new HashMap<>(); //create a map with key:tag name value: tag count

        String[] keyWords = title.split(" ");
        for (String word : keyWords) {
            if (!stopWords.contains(word.toLowerCase())) {
                Map<String, String> res = jedis.hgetAll("questionTag:" + word.toLowerCase());
                if (res != null) {
                    for(String key : res.keySet()) {
                        String tagsCnt = res.get(key);

                        //example:*****WrappedArray([scheme,1], [stream,1])
                        //manipulate string result to get a list of array[tag, cnt]
                        //step 1: delete "WrappedArray(" and the last ")"
                        String result = tagsCnt.substring(13, tagsCnt.length() - 1);
                        //Step 2. split the result into array
                        String[] resultInd = result.split(", ");
                        for (String element : resultInd) {
                            //Step 3: for each element in the array, manipulate each string and
                            // extract key: tag and value count and store them into the info map
                            String[] keyAndVal = element.substring(1, element.length() - 1).split(",");
                            String keyTag = keyAndVal[0];
                            Integer tagCnt = Integer.valueOf(keyAndVal[1]);
                            if (tagMap.containsKey(keyTag)) {
                                Integer origVal = tagMap.get(keyTag);
                                tagMap.put(keyTag, origVal + tagCnt);
                            } else {
                                tagMap.put(keyTag, tagCnt);
                            }
                        }

                    };
                }
            }
        }

        ValueComparator bvc = new ValueComparator(tagMap);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        sorted_map.putAll(tagMap);

        TreeMap<String, Integer> topTags = sorted_map
                .entrySet()
                .stream()
                .limit(3)
                .collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
        return topTags.keySet();
    }

    public static Set<String> stopWords = new HashSet<String>(Arrays.asList(
            "and", "an", "i", "not", "is", "are", "?",
            ".", "who", "in", "to", "how", "a", "the", "using" ));

    static class ValueComparator implements Comparator<String> {
        Map<String, Integer> base;

        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }
}

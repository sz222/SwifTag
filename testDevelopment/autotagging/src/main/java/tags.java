/* This class digest sample tags data, aggregate on 'tag' and write (tag, count) pair into redis table */
//package autoTagging;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;



public class tags {
    public static void main(String[] args) {
        //ingest sample data of 2000 records from S3
        String inputPath = "s3a://insightdeshuyan/tags/questions_sample_2k.csv";
        //start a new spark session
        SparkSession spark = SparkSession.builder()
                .appName("Tag Count")
                .config("spark.redis.host", "10.0.0.12")
                .config("spark.redis.port", "6379")
                .getOrCreate();
        
        spark.stop();
    }
}
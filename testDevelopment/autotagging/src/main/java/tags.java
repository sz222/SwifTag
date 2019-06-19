/* This class digest sample tags data, aggregate on 'tag' and write (tag, count) pair into redis table */
//package autoTagging;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

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
             //config to read int csv file
        Dataset<Row> data = spark.read()
                .option("multiLine", true)
                .option("header", true)
                .option("escape", "\"")
                .csv(inputPath);
        //apply split and explode function to field 'tags' and flat one single column "tags" into multiple rows of field 'tag'
        Dataset<Row> tags = data.select(
                org.apache.spark.sql.functions.explode(org.apache.spark.sql.functions.split(data.col("tags"), "\\|"))
                        .as("tag")
        );
        //did aggregation count based on 'tag' field
        Dataset<Row> counts = tags.groupBy("tag").count();
        counts.orderBy(counts.col("count").desc());
        //write (key, value) pair into redis table called 'tag'
        counts.write()
                .format("org.apache.spark.sql.redis")
                .option("table", "tag")
                .option("key.column", "tag")
                .mode(SaveMode.Overwrite)
                .save();
        
        spark.stop();
    }
}
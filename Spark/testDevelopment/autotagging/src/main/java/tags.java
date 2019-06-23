import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import java.lang.String;

import static org.apache.spark.sql.functions.*;

public class tags {
    public static String[] stopWords = { "and", "an", "i", "not", "is", "are", "?", ".", "who", "in", "to", "how", "a", "the" };

    public static void main(String[] args) {
        //ingest sample data of 2000 records from S3
//         String inputPath = "s3a://insightdeshuyan/tags/questions_sample_2k.csv";
        String inputPath = "s3a://insightdeshuyan/questiondata/all-00000000000*.csv";
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

        Dataset<Row> tags = data.withColumn(
                "tag", explode(split(data.col("tags"), "\\|")).as("tag"));

        tags.createOrReplaceTempView("tags");
        //select field: id, title, tag from data:tags
        Dataset<Row> prepData = spark.sql("SELECT id, title, tag, tags FROM tags");
        //split and extract key words from question "title" field

        Dataset<Row> keyWords = prepData.withColumn(
                "keyWord", explode(split(prepData.col("title"), " ")).as("keyWord"));
        //remove some major meaningless key words from the data
        Dataset<Row> keyWordsCleaned = keyWords.withColumn(
                "keyWordLower", lower(keyWords.col("keyWord")).as("keyWordLower"));
        keyWordsCleaned = keyWordsCleaned.filter(
            not(keyWordsCleaned.col("keyWordLower").isin(stopWords))
        );


        Dataset<Row> counts = keyWordsCleaned.groupBy("tag", "keyWordLower").count(); //, "keyWordLower"
        Dataset<Row> countsOrd = counts.orderBy(counts.col("count").desc());
//        //write (key, value) pair into redis table called 'tag'
        countsOrd.write()
                .format("org.apache.spark.sql.redis")
                .option("table", "tag")
                .option("key.column", "tag")
                .mode(SaveMode.Overwrite)
                .save();
        countsOrd.show(30);
        keyWordsCleaned.schema();
        spark.stop();
    }
}

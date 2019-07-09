import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import java.lang.String;

import static org.apache.spark.sql.functions.*;

public class tags {
    public static String[] stopWords = { "and", "an", "i", "not", "is", "are", "?",
                                        ".", "who", "in", "to", "how", "a", "the", "using" };

    public static void main(String[] args) {
        //ingest all the data files in s3
        String inputPath = "s3a://insightdeshuyan/questiondata/all-0000000000**.csv";

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

        //flatten map tags column
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

        //count number of records group by tag and keyWordLower fileds together
        Dataset<Row> tagsCount = keyWordsCleaned.groupBy("tag", "keyWordLower").count();

        //for each keyWord, construct an array of tuples of (tag, count) inside
        Dataset<Row> tagsTuple = tagsCount.withColumn("tagTuple",
                struct(tagsCount.col("tag"), tagsCount.col("count")).as("tagTuple"));//.show(false)

        Dataset<Row> tagsList = tagsTuple.groupBy("keyWordLower").agg(collect_list("tagTuple")); //tagsTuple.col(

//        //write (key, value) pair into redis table called 'questionTag'
        tagsList.write()
                .format("org.apache.spark.sql.redis")
                .option("table", "questionTag")
                .option("key.column", "keyWordLower")
                .option("value.column", "tagList")
                .mode(SaveMode.Overwrite)
                .save();
        tagsList.show(30);
        tagsList.schema();
        spark.stop();
    }
}

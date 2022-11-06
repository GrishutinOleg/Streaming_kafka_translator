import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, LongType, StringType, StructField, StructType}



object Stream_translator extends App {

  val spark = SparkSession
    .builder()
    .appName("Streaming kafka translator")
    .config("spark.master", "local")
    .getOrCreate()

  val topicinput = "items_generated"

  val itemsschema = new StructType()
    //.add("Datecur", StringType)
    //.add("Timecur", StringType)
    .add("Curts", LongType)
    .add("X", IntegerType)
    .add("Y", IntegerType)
    .add("Z", IntegerType)

  import spark.implicits._
  val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", topicinput)
    //.option("startingOffsets", "latest")
    .option("startingOffsets", "earliest")
    .load()

  df.printSchema()

  //val df1 =
  //df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
   // .as[(String, String)]
    //.cache()
    //.localCheckpoint()

  /*val df3 = df1.select(from_json(col("value"), itemsschema).as("data")).select("data.*")


  val df4 = df3.writeStream
    .format("console")
    .outputMode("append")
    .start()
    .awaitTermination()
*/

  //df.show()

  val topicoutput = "items_translated"

  val ds = df
    //.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
    .selectExpr("to_json(struct(*)) AS value")
    .writeStream
    .format("kafka")
    .outputMode("append")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("topic", topicoutput)
    .start()
    .awaitTermination()



}

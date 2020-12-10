//startingOffsets:  earliest or latest
val df =spark.readStream.format("kafka").option("kafka.bootstrap.servers", "kafka:9092").option("subscribe", "Hello-JSON").option("startingOffsets", "latest").load()

//returns the schema of streaming data from Kafka. The returned DataFrame contains all the familiar fields of a Kafka record and its associated metadata.
df.printSchema()
//Since the value is in binary, first we need to convert the binary value to String using selectExpr()
val personStringDF = df.selectExpr("CAST(value AS STRING)")
//personStringDF.writeStream .format("console") .outputMode("append") .start() .awaitTermination()
//Now, extract the value which is in JSON String to DataFrame and convert to DataFrame columns using custom schema.
import org.apache.spark.sql.types._
val schema = new StructType() .add("id",IntegerType) .add("firstname",StringType) .add("middlename",StringType) .add("lastname",StringType) .add("dob_year",IntegerType) .add("dob_month",IntegerType) .add("gender",StringType) .add("salary",IntegerType) 
 val personDF = personStringDF.select(from_json(col("value"), schema).as("data")) .select("data.*")

personDF.writeStream .format("console") .outputMode("append") .start() .awaitTermination()



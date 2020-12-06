val df =spark.readStream.format("kafka").option("kafka.bootstrap.servers", "kafka:9092").option("subscribe", "Hello-Kafka").option("startingOffsets", "latest").load()

df.printSchema()
val personStringDF = df.selectExpr("CAST(value AS STRING)")
import org.apache.spark.sql.types._
val schema = new StructType() .add("id",IntegerType) .add("firstname",StringType) .add("middlename",StringType) .add("lastname",StringType) .add("dob_year",IntegerType) .add("dob_month",IntegerType) .add("gender",StringType) .add("salary",IntegerType) 
 val personDF = personStringDF.select(from_json(col("value"), schema).as("data")) .select("data.*")

personDF.writeStream .format("console") .outputMode("append") .start() .awaitTermination()


package com.lucas.demo.union
import java.sql.Timestamp

import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{StructField, StructType}
import scala.collection.JavaConverters._
import org.apache.spark.sql._
import java.sql.Timestamp
import org.apache.spark.sql.types._

object GenerateDemoDataInMySQL {
  def main(args: Array[String]): Unit = {
    //val sparkConf = new SparkConf().setAppName("Web Page Popularity Value Calculator")
    //val spark = SparkSession.builder.appName("My Application").getOrCreate();

    val spark: SparkSession = Tools.getSparkSession
    import spark.implicits._

    recreateTable("product");
    val connectionProperties = new java.util.Properties();
    connectionProperties.put("user", "root");
    connectionProperties.put("password", "secret");
    connectionProperties.put("partitionColumn", "id");
    connectionProperties.put("fetchsize", "3");
    connectionProperties.put("batchsize", "1000");
    connectionProperties.put("lowerBound", "1");
    connectionProperties.put("upperBound", "10000000");
    connectionProperties.put("numPartitions", "10");
    connectionProperties.put("driver", "com.mysql.cj.jdbc.Driver");
    val df = spark.read.jdbc("jdbc:mysql://db_mysql", "lucas_test.product", connectionProperties)
    // Looks the schema of this DataFrame.

    df.printSchema()

    // Counts people by age
    val countsByAge = df.groupBy("name").count()
    countsByAge.show()

    //组装结果RDD
    val arr = new Array[Long](100000)
    for (i<-0 to (arr.length -1 )){
      //arr(i) = f"item_$i%06d"
      arr(i) = i
    }

    val arrayRDD = spark.sparkContext.parallelize(arr)
    //将结果RDD映射到rowRDD
    val resultRowRDD = arrayRDD.map(p =>Row(
      p,
      "name_"+ p,
      new Timestamp(new java.util.Date().getTime)
    ))
    //通过StructType直接指定每个字段的schema
    val resultSchema = StructType(
      List(
        StructField("id", LongType, true),
        StructField("name", StringType, true), //是哪一天日志分析出来的结果
        StructField("tstamp", TimestampType, true) //分析结果的创建时间
      )
    )
    //组装新的DataFrame
    val DF = spark.createDataFrame(resultRowRDD,resultSchema)
    //将结果写入到Mysql
    DF.write.mode(SaveMode.Append).jdbc("jdbc:mysql://db_mysql:3306", "lucas_test.product", connectionProperties)
  }

  def recreateTable(tableName: String) = {

      import java.sql._;

      var conn: Connection = null;
      var stmt: Statement = null;

      try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        println("Connecting to a selected database...");
        conn = DriverManager.getConnection("jdbc:mysql://db_mysql:3306/lucas_test","root", "secret");
        println("Connected database successfully...");
        println("Deleting table in given database...");
        stmt = conn.createStatement();
        val sql: String = s"DROP TABLE ${tableName} ";
        stmt.executeUpdate(sql);
        println(s"Table ${tableName} deleted in given database...");
      } catch {
        case e: Exception => println("exception caught: " + e);
      } finally {
        stmt = conn.createStatement();
        println("Cretae table product");
        stmt.executeUpdate(s"create table ${tableName} (id INTEGER, name VARCHAR(20), tstamp TIMESTAMP)");
        println(s"Table ${tableName} created in given database...");
      }
  }

}

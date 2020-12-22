package com.lucas.demo.union
import org.apache.spark.sql._
import java.sql.Timestamp
import org.apache.spark.sql.types._

import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{StructField, StructType}
import scala.collection.JavaConverters._

object SplitIngester {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = Tools.getSparkSession

    /*
    val query = """
      (select * from product where id < 'item_000100') product
    """
    val options = Map(
        "url"              -> "jdbc:mysql://db_mysql/lucas_test",
        "user"             -> "root",
        "password"         -> "secret",
        "dbtable"          -> query,
    )

    val nearData = spark.read.format("jdbc").options(options).load()
    */

    val allQuery = "product";

    val allOptions = Map(
        "url"              -> "jdbc:mysql://db_mysql/lucas_test",
        "user"             -> "root",
        "password"         -> "secret",
        "driver"         -> "com.mysql.cj.jdbc.Driver",
        "dbtable"          -> allQuery,
    )

    val allData = spark.read.format("jdbc").options(allOptions).load()
    import spark.implicits._

    println("========INGEST  BEGIN====")
    println("Get near data from MyQL");
    val nearData = allData.filter($"id" < 100)
    println("Connect to kudu");
    val kuduContext = new KuduContext(" kudu-master-1:7051,kudu-master-2:7151,kudu-master-3:7251", spark.sparkContext)
    val nearDataKuduTableName = "nearData"

    println(s"Delete if exist: Kudu table ${nearDataKuduTableName}");
    if(kuduContext.tableExists(nearDataKuduTableName)) {
      kuduContext.deleteTable(nearDataKuduTableName)
    }

    val resultSchema = StructType(
      List(
        StructField("id", IntegerType, false),
        StructField("name", StringType, false),
        StructField("tstamp", TimestampType, false)
      )
    )

    import scala.collection.JavaConverters._
    import org.apache.kudu.client.CreateTableOptions

    println(s"Create: Kudu table ${nearDataKuduTableName}");
    kuduContext.createTable(nearDataKuduTableName,
      resultSchema, // Kudu schema with PK columns set as Not Nullable
      Seq("id", "name"), // Primary Key Columns
      new CreateTableOptions().
        setNumReplicas(3).
        addHashPartitions(List("id", "name").asJava, 2))
    println(s"insertRows to kudu talbe ${nearDataKuduTableName}");
    kuduContext.insertRows(nearData, nearDataKuduTableName)


    println("Get far data from MyQL");
    val farData = allData.filter($"id" >= 100)
    println("Create table in HIVE: farHiveData");
    //spark.sql("DROP TABLE farHiveData")
    spark.sql("CREATE TABLE IF NOT EXISTS farHiveData (id INT, name STRING, tstamp TIMESTAMP) USING hive")
    println("Save farData to farHiveData");
    farData.write.mode(SaveMode.Overwrite).saveAsTable("farHiveData")

    println("========INGEST  END====")

    println("========LOAD/UNION/ANALYSIS  BEGIN====")
    println(s"Load near data from Kudu");
    val nearDataKuduDf = spark.read.option("kudu.master", "kudu-master-1:7051,kudu-master-2:7151,kudu-master-3:7251"). option("kudu.table", nearDataKuduTableName). option("kudu.scanLocality", "leader_only"). format("kudu"). load
    println(s"Load far data from Hive");
    val farDataHiveDf = spark.sql("select * from farHiveData")


    println(s"Combine Kudu and Hive data");
    val allDataDf = nearDataKuduDf.union(farDataHiveDf)


    println(s"Analysis...");
    nearDataKuduDf.createOrReplaceTempView("nearProduct");
    farDataHiveDf.createOrReplaceTempView("farProduct");
    allDataDf.createOrReplaceTempView("allProducts")

    val cntNear = spark.sql(""" select count(*) cnt from nearProduct """).select("cnt").map(r=>r.getLong(0)).collect.toList(0)
    val cntFar = spark.sql(""" select count(*) cnt from farProduct """).select("cnt").map(r=>r.getLong(0)).collect.toList(0)
    val cntAll = spark.sql(""" select count(*) cnt from allProducts """).select("cnt").map(r=>r.getLong(0)).collect.toList(0)
    println(s"nearData: $cntNear, farData: $cntFar,  allData: $cntAll")
    println("========LOAD/UNION/ANALYSIS  END====")

  }


  // C/O https://github.com/apache/kudu/tree/master/examples/quickstart/spark#load-and-prepare-the-csv-data
  def setNotNull(df: DataFrame, columns: Seq[String]) : DataFrame = {
    val schema = df.schema
    // Modify [[StructField] for the specified columns.
    val newSchema = StructType(schema.map {
      case StructField(c, t, _, m)
        if columns.contains(c) => StructField(c, t, nullable = false, m)
      case y: StructField => y
    })
    // Apply new schema to the DataFrame
    df.sqlContext.createDataFrame(df.rdd, newSchema)
  }
}


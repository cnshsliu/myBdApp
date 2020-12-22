package com.lucas.demo.view
import org.apache.spark.sql.{DataFrame, SparkSession}

object TempView {
  def main(args: Array[String]): Unit = {

    val spark: SparkSession = getSparkSession

    val logData = spark.read.textFile("/tmp/README.txt").cache()
    //创建全局临时视图readmedata
    logData.createGlobalTempView("readmedata")
    //创建临时视图foo
    spark.range(1).createTempView("foo")

    // 在当前 session 中应能找到 foo
    println("""spark.catalog.tableExists("foo") = """ + spark.catalog.tableExists("foo"))
    //spark.catalog.tableExists("foo") = true

    // 在新建的session中，foo不存在
    val newSpark = spark.newSession
    println("""newSpark.catalog.tableExists("foo") = """ + newSpark.catalog.tableExists("foo"))
    //newSpark.catalog.tableExists("foo") = false

    // 两个session都可以访问到全局临时视图readmedata
    spark.sql("SELECT * FROM global_temp.readmedata").show()
    newSpark.sql("SELECT * FROM global_temp.readmedata").show()

    spark.stop()
  }

  private def getSparkSession = {
    val spark = SparkSession.builder()
      .appName("Prepare Data")
      .master("local")
      .getOrCreate()

    spark.
      sparkContext.
      setLogLevel("WARN")
    spark
  }
}

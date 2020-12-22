package com.lucas.demo.union
import org.apache.spark.sql.{DataFrame, SparkSession}

object Tools{
  def getSparkSession = {
    val spark = SparkSession.builder()
      .appName("Lucas Kehong Liu Spark Demo")
      .master("local")
      .enableHiveSupport()
      .getOrCreate()

    spark.
      sparkContext.
      setLogLevel("ERROR")
    spark
  }
}

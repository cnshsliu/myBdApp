package com.lucas.demo.union
import org.apache.spark.sql.SparkSession

object UnionExample extends App{
  val spark: SparkSession = SparkSession.builder()
    .master("local[1]")
    .appName("Lucas Kehong Liu Spark Demo")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")
  import spark.implicits._

  val simpleData = Seq(
    ("张三","销售","北京",90000,34,10000),
    ("李四","销售","北京",86000,56,20000),
    ("王五","销售","深圳",81000,30,23000),
    ("老刘","财务","深圳",90000,24,23000)
  )
  val df = simpleData.toDF("employee_name","department","state","salary","age","bonus")
  df.printSchema()
  df.show()


  val simpleData2 = Seq(
    ("张三","销售","北京",90000,34,10000),
    ("陈七","财务","深圳",90000,24,23000),
    ("孙八","财务","北京",79000,53,15000),
    ("王五","销售","深圳",81000,30,23000),
    ("赵四","市场","深圳",80000,25,18000),
    ("杨九","市场","北京",91000,50,21000)
  )
  val df2 = simpleData2.toDF("employee_name","department","state","salary","age","bonus")
  df2.show()

  val df3 = df.union(df2)
  df3.show()


  val df4 = df.union(df2).distinct()
  df4.show()
}

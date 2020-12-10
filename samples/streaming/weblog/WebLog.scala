package com.lucas.spark.streaming;
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object WebLog {
  def main(args: Array[String]) {
    val batch = 10
    var total = 0
    val configuration = new Configuration();
    val conf = new SparkConf().setAppName("NginxAnay")
    val ssc = new StreamingContext(conf, Seconds(batch))

    ssc.checkpoint("hdfs:///spark/streaming/checkpoint")

    val lines = ssc.textFileStream("hdfs:///spark/streaming/weblog");

    // 总PV
    lines.count().print()
    //lines.countByWindow(Seconds(batch*6), Seconds(batch*6)).print()

    // 各IP的PV， 按PV倒序， 空格分隔的第一个字段就是IP
    lines.map(line => {(line.split(" ")(0), 1)}).reduceByKey(_ + _).transform(rdd=>{
      //次数为key，
      rdd.map(ip_pv => (ip_pv._2, ip_pv._1))
      //按次数降序排列
      .sortByKey(false)
      //再换位ip为key
      .map(ip_pv => (ip_pv._2, ip_pv._1))
    }).print()

    def updateFunction(newValues: Seq[Int], runningCount: Option[Int]): Option[Int] = {
      val preCount = runningCount.getOrElse(0)
      val newCount = newValues.sum
      val res = newCount + preCount
      Some(newCount + preCount)
    }

    val accesses = lines.map(line=>(line.split(" ")(0), 1));
    //val accesses = lines.map(line=>("access", 1));

    val accessCounts = accesses.updateStateByKey[Int](updateFunction _).print()

    // 搜索引擎PV
    val refer = lines.map(_.split("\"")(3))
    //先输出搜索引擎和查询关键词，避免统计搜索关键词时重复计算
    //输出（host, query_keys)
    val searchEnginInfo = refer.map(r=>{
      var f = r.split('/')
      val searchEngines = Map(
        "www.google.cn"   -> "q",
        "www.yahoo.com"   -> "p",
        "cn.bing.com"     -> "q",
        "www.baidu.com"   -> "wd",
        "www.sogou.com"   -> "query"
      )
      
      if (f.length > 2){
        val host = f(2)
        if (searchEngines.contains(host)){
          val query = r.split('?')(1)
          if (query.length > 0){
            val arr_search_q = query.split('&').filter(_.indexOf(searchEngines(host)+"=") == 0)
            if(arr_search_q.length > 0)
              (host, arr_search_q(0).split('=')(1))
            else
              (host, "")
          }else
            (host, "")
        }else
          ("", "")
      }else
        ("", "")
    });

    // 输出搜索引擎PV
    searchEnginInfo.filter(_._1.length>0).map(p=>{(p._1, 1)}).reduceByKey(_+_).print()

    // 关键词PV
    searchEnginInfo.filter(_._2.length>0).map(p=>{(p._2, 1)}).reduceByKey(_+_).print()

    // 终端类型PV
    lines.map(_.split("\"")(5)).map(agent=>{
      val types = Seq("iPhone", "Android")
      var r = "Default"
      for (t <- types){
        if(agent.indexOf(t) != -1)
          r = t
      }
      (r, 1)
    }).reduceByKey(_+_).print()

    // 各页面PV
    lines.map(line => {(line.split("\"")(1).split(" ")(1),1)}).reduceByKey(_+_).print()

    //启动计算，等待执行结束（出错或Ctrl-C退出）
    ssc.start()
    ssc.awaitTermination()

  }
}

FROM bde2020/spark-scala-template:3.0.1-hadoop3.2
MAINTAINER Lucas Liu <liukehong@gmail.com>
COPY log4j.properties /spark/conf
COPY spark-env.sh /spark/conf
COPY core-site.xml /etc/hadoop/core-site.xml
COPY hdfs-site.xml /etc/hadoop/hdfs-site.xml
ENV SPARK_MASTER_NAME spark-master
ENV SPARK_MASTER_PORT 7077
ENV SPARK_APPLICATION_MAIN_CLASS com.lucas.MyApp
ENV SPARK_APPLICATION_ARGS "foo bar baz"
ENV CORE_CONF_fs_defaultFS=hdfs://namenode:8020


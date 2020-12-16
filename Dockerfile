FROM lucas/spark-scala-template
MAINTAINER Lucas Liu <liukehong@gmail.com>

USER root
ENV SPARK_MASTER_NAME spark-master
ENV SPARK_MASTER_PORT 7077
ENV SPARK_APPLICATION_MAIN_CLASS com.lucas.MyApp
ENV SPARK_APPLICATION_ARGS "foo bar baz"
ENV CORE_CONF_fs_defaultFS=hdfs://namenode:8020



WORKDIR /app

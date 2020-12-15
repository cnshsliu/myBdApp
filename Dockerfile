FROM lucas/spark-scala-template
MAINTAINER Lucas Liu <liukehong@gmail.com>

USER root

COPY resources/hadoop-3.2.1.tar.gz /tmp/hadoop-3.2.1.tar.gz
WORKDIR /tmp
RUN tar xzf hadoop-3.2.1.tar.gz && \
    rm -rf /hadoop 2>/dev/null  && \
    mv hadoop-3.2.1 /hadoop && \
    rm /tmp/hadoop-3.2.1.tar.gz


COPY conf/bashrc /root/.bashrc
COPY etc/apt/sources.list /etc/apt/sources.list
COPY conf/sbt.repositories /root/.sbt/repositories
COPY etc/hadoop/core-site.xml /hadoop/etc/hadoop/core-site.xml
COPY etc/hadoop/hdfs-site.xml /hadoop/etc/hadoop/hdfs-site.xml
COPY etc/hadoop/mapred-site.xml /hadoop/etc/hadoop/mapred-site.xml
COPY etc/hadoop/yarn-site.xml /hadoop/etc/hadoop/yarn-site.xml

COPY conf/hive-site.xml /spark/conf/hive-site.xml
COPY conf/spark-env.sh /spark/conf/spark-env.sh
COPY conf/log4j.properties /spark/conf/log4j.properties
ENV SPARK_MASTER_NAME spark-master
ENV SPARK_MASTER_PORT 7077
ENV SPARK_APPLICATION_MAIN_CLASS com.lucas.MyApp
ENV SPARK_APPLICATION_ARGS "foo bar baz"
ENV CORE_CONF_fs_defaultFS=hdfs://namenode:8020



WORKDIR /app
RUN rm -rf /app/download 2>/dev/null  && \
    rm /app/*.sh 2>/dev/null && \
    rm -rf /app/etc 2>/dev/null && \
    rm -rf /app/conf 2>/dev/null  && \
    rm  /app/Dockerfile && \
    rm  /app/Dockerfile.template && \
    rm -rf /app/resources

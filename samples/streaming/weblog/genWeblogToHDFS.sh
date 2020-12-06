#!/bin/bash

HDFS="/hadoop/bin/hadoop fs"

streaming_dir="/spark/streaming"
$HDFS -rm "${streaming_dir}"'/tmp/*'    >/dev/null 2>&1
$HDFS -rm "${streaming_dir}"'/*'        >/dev/null 2>&1

while [ 1 ]; do
  ./sample_web_log.py  > test.log
  
  tmplog="access.`date +'%s'`.log"
  $HDFS -put test.log ${streaming_dir}/tmp/$tmplog
  $HDFS -mv           ${streaming_dir}/tmp/$tmplog ${streaming_dir}/

  echo "`date +"%F %T"` put $tmplog to HDFS succeed"
  sleep 1
done

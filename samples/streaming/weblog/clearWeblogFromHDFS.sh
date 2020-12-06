#!/bin/bash

HDFS="/hadoop/bin/hadoop fs"

streaming_dir="/spark/streaming"
$HDFS -rm "${streaming_dir}"'/tmp/*'    >/dev/null 2>&1
$HDFS -rm "${streaming_dir}"'/*'        >/dev/null 2>&1

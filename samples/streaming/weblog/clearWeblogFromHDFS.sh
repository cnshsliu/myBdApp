#!/bin/bash

HDFS="/hadoop/bin/hadoop fs"

streaming_dir="/spark/streaming"
$HDFS -rm "${streaming_dir}"'/checkpoint/*'    >/dev/null 2>&1
$HDFS -rm "${streaming_dir}"'/tmp/*'    >/dev/null 2>&1
$HDFS -rm "${streaming_dir}"'/weblog/*'        >/dev/null 2>&1

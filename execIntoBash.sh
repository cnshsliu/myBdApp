docker exec -it  \
  -e ENABLE_INIT_DAEMON=false \
  -e CORE_CONF_fs_defaultFS=hdfs://namenode:8020 \
  myapp1 \
  /bin/bash

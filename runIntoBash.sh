docker run -it --rm \
  --name my-app-bash \
  -e ENABLE_INIT_DAEMON=false \
  -e CORE_CONF_fs_defaultFS=hdfs://namenode:8020 \
  --network bd-infra_net_pet \
  --link spark-master:spark-master  \
  -v /Users/lucas/dev/BigData/myApp/datasets/:/app/datasets \
  lucas/myapp \
  /bin/bash

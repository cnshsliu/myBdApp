docker run -it --rm \
  --name myapp1 \
  -e ENABLE_INIT_DAEMON=false \
  --env-file ./config/hadoop-hive.env \
  --network bdi_net_lucas \
  --link spark-master:spark-master  \
  lucas/myapp \
  /spark/bin/spark-shell

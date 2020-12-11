docker run --rm \
  --name my-app-1 \
  -e ENABLE_INIT_DAEMON=false \
  --env-file ./config/hadoop-hive.env \
  --network bdi_net_lucas \
  --link spark-master:spark-master \
  lucas/myapp

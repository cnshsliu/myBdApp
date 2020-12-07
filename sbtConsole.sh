docker run -it --rm \
  --name SbtConsole \
  -e ENABLE_INIT_DAEMON=false \
  --network bdi_net_lucas \
  --link spark-master:spark-master  \
  lucas/myapp \
  sbt console

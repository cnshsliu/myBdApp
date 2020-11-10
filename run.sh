docker run --rm \
  --name my-app-1 \
  -e ENABLE_INIT_DAEMON=false \
  --network bd-infra_net_pet \
  --link spark-master:spark-master \
  lucas/myapp

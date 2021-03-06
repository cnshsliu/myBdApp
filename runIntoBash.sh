docker run -it --rm \
  --name myapp1 \
  -e ENABLE_INIT_DAEMON=false \
  --env-file ./config/hadoop-hive.env \
  -v ~/bdi/resources/:/bdi/resources/ \
  -v ~/myBdApp/src/:/app/src/ \
  -v ~/myBdApp/lib/:/app/lib/ \
  -v ~/myBdApp/bin/:/app/bin/ \
  --network bdi_net_lucas \
  --link spark-master:spark-master  \
  lucas/myapp \
  /bin/bash

docker run --name zookeeper -d \
-p 2181:2181 \
-e ZOOKEEPER_CLIENT_PORT=2181 \
confluentinc/cp-zookeeper:7.0.1
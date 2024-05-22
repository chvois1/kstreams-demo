#! /bin/bash

$KAFKA_HOME/bin/kafka-topics --create --bootstrap-server localhost:9092 --topic achats --replication-factor 1 --partitions 8
$KAFKA_HOME/bin/kafka-topics --create --bootstrap-server localhost:9092 --topic achats-by-product-id --replication-factor 1 --partitions 8
$KAFKA_HOME/bin/kafka-topics --create --bootstrap-server localhost:9092 --topic achats-enrichis --replication-factor 1 --partitions 8
$KAFKA_HOME/bin/kafka-topics --create --bootstrap-server localhost:9092 --topic referentiel --replication-factor 1 --partitions 8 --config cleanup.policy=compact

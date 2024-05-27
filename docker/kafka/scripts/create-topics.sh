#! /bin/bash

$KAFKA_HOME/bin/kafka-topics --create --bootstrap-server localhost:9092 --topic demandes --replication-factor 1 --partitions 8
$KAFKA_HOME/bin/kafka-topics --create --bootstrap-server localhost:9092 --topic demandes-by-usager-id --replication-factor 1 --partitions 8
$KAFKA_HOME/bin/kafka-topics --create --bootstrap-server localhost:9092 --topic demandes-enrichies --replication-factor 1 --partitions 8
$KAFKA_HOME/bin/kafka-topics --create --bootstrap-server localhost:9092 --topic usagers --replication-factor 1 --partitions 8 --config cleanup.policy=compact

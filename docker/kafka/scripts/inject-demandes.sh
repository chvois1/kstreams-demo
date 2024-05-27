#! /bin/bash

echo '{"id": 1, "name": "usager", "firstName": "usr01"}
{"id": 2, "name": "usager", "firstName": "usr02"}
{"id": 30, "name": "usager", "firstName": "usr01"}
{"id": 1, "name": "usager", "firstName": "usr-01"}' | $KAFKA_HOME/bin/kafka-console-producer --bootstrap-server localhost:9092 localhost:9092 --topic demandes

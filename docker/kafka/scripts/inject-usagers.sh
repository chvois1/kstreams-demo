#! /bin/bash

echo '1,{"id":1, "email":"usager.001@mail.com"}
2,{"id":2, "email":"usager.002@mail.com"}
3,{"id":3, "email":"usager.003@mail.com"}' | $KAFKA_HOME/bin/kafka-console-producer --bootstrap-server localhost:9092 localhost:9092 --topic usagers --property parse.key=true --property key.separator=,


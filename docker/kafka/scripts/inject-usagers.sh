#! /bin/bash

echo '1,{"id":1, "eMail":"usager.001@mail.com"}
2,{"id":2, "eMail":"usager.002@mail.com""}
3,{"id":3, "eMail":"usager.003@mail.com""}' | $KAFKA_HOME/bin/kafka-console-producer --bootstrap-server localhost:9092 localhost:9092 --topic referentiel --property parse.key=true --property key.separator=,


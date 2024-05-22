# Démo KStreams

Deux modes d'installation de Zookeeper, Kafka et de l'application sont possibles : avec ou sans Docker.

Pré-requis : Git et Maven.

Cloner le projet git :

    git clone https://github.com/tmouron/kafka-dix-demo.git

## Démo

Builder le projet :

    cd kafka-dix-demo
    mvn package

Lancer le cluster :

    cd docker
    cp ../target/kafka-dix-1.0-jar-with-dependencies.jar app/
    docker compose up -d

### Créer les topics

docker exec $(docker ps | grep kafka | awk {'print $1'} | head -1) bash -c "/opt/scripts/create-topics.sh"

### Référentiel

Injecter des produits dans le topic `referentiel` :

    docker exec $(docker ps | grep kafka | awk {'print $1'} | head -1) bash -c "/opt/scripts/inject-referentiel.sh"

Cela injecte les messages (clé,valeur) suivants dans le référentiel :

    1,{"id":1, "name":"produit1"}
    2,{"id":2, "name":"produit2"}
    3,{"id":3, "name":"produit3"}

### Achats

Injecter des produits dans le topic `achats` :

    docker exec $(docker ps | grep kafka | awk {'print $1'} | head -1) bash -c "/opt/scripts/inject-achats.sh"

Cela injecte les messages suivants dans le flux des achats :

    {"id": 1, "price": 3.45}
    {"id": 2, "price": 13.40}
    {"id": 30, "price": 1.05}
    {"id": 1, "price": 3.40}

Ici la clé n'est pas présente, ce qui revient à produire le message dans une partition aléatoire.

### Résultat

Consommer les messages présents dans le topic `achats-enrichis` :

    docker exec $(docker ps | grep kafka | awk {'print $1'} | head -1) bash -c "/opt/scripts/consume-output.sh"

Output :

    {"id":2,"name":"produit2","price":13.40}
    {"id":30,"name":"REF INCONNUE","price":1.05}
    {"id":1,"name":"produit1","price":3.40}
    {"id":1,"name":"produit1","price":3.45}

On voit que les achats ont été enrichis du libellé produit grâce au référentiel.

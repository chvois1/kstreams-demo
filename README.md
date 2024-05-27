# Kafka stream processing

## Pourquoi faire ?

Mettre en place un framework de stream processing implique le plus souvent:  

- Un middlewares supplémentaire sous la forme d'un système master/slave.
- Un gestionnaire de ressources.
- Un grand nombre de dépendances.
- Une nouvelle API à maîtriser
- Plus de complexité opérationnelle ...

À l’inverse, utiliser les librairies clientes de Kafka et traiter les données *à la main* en développant sa propre application reste assez fastidieux. Gérer la distribution, le sharding, l’ajout ou le retrait de ressources à chaud, la reprise sur erreur ou le redéploiement applicatif sont de vrais sujets.

Kafka Streams est une solution intermédiaire. Il s'agit d'une librairie très *légère* qui repose entièrement sur les clients Kafka. Kstreams offre également une API nommé  *Kafka Streams DSL* pour décrire d’une manière fonctionnelle les opérations à effectuer. KStreams DSL repose sur l’API en Java 8, ce qui permet d'écrire du code fonctionnel notamment grâce aux lambdas.

## Exemple de mise en oeuvre

Le scénario est un usager qui demande à renouveler son mot de passe.

- Le flot Kafka initial alimente un topic où chaque événement contient un champ qui identifie sans équivoque l'usager demandeur.
- Une table des usagers permet d'associer les caractéristiques d'un usager à son identifiant.
- Le flot en sortie sera dénormalisé à l'aide d'une jointure entre l'identifiant de l'usager demandeur (contenu dans la requête initiale) et l'adresse mail de cet usager (contenu dans un champ de la table usager).  

## KStream vs KTable

Deux types de streams existent dans les API fournies par kafka: les KStreams et les KTables. Les KTables sont des Streams finis, ou encore selon la nomenclature de Kafka des streams compactés. Alors qu’un KStream représente un topic Kafka infini, une KTable est un topic pour lequel chaque nouveau message clé/valeur écrase l’ancien message avec la même clé, ou insère le message si la clé est nouvelle. Il s'agit d'une Map <clé,valeur>.

![Aperçu](images/kstream-ktable.png "Aperçu")

### Produits/Libellés

Problème: associer une table d'identifiants produits avec les libellés des produits correspondants.  

Un référentiel de mappings entre des identifiants de produits et les libellés associés peut être représenté par une KTable. Si l’on décide de mettre à jour le libellé d’un produit du référentiel, l’injection d’un nouveau message écrasera l’ancien. La politique de log compaction de Kafka nous garantit que nos tuples ne seront pas effacés, contrairement à un topic non compacté qui voit ses messages effacés après 7 jours, par défaut.

Lorsqu’une application travaillant avec des KTables démarre, elle lit entièrement le topic depuis l’offset où elle s’était arrêtée (de la fin par défaut, si elle démarre pour la première fois), puis stocke les messages dans une instance RocksDB qui est un cache persisté et local à chaque JVM. Elle reste ensuite à l’écoute du topic pour insérer tout nouveau message dans son cache.

Pour une instance d’une application KStreams, joindre un KStream(topic achats) et une KTable(topic usagers) revient donc à :

- lire et mémoriser dans un cache l’ensemble des partitions assignées du topic réferentiel.
- joindre chaque tuple provenant des partitions assignées du topic achats à la volée avec un tuple de la KTable référentiel.

![Aperçu](images/workflow.png "Aperçu")

## Démonstration

### Pré-requis

Git et Maven.

### Installation

Cloner le projet git et fabriquer les paquets.

```bash
git clone https://github.com/chvois1/kstreams-demo.git
```

Builder le projet :

```bash
cd kstreams-demo
mvn package
```

Lancer le cluster :

```bash
cd docker
docker compose up 
```

### Connexion au container

Pour visuliser les résultats, il faut se connecter dans le container en exécution. Les instructions suivantes sont inefficaces car elles créent une nouvelle session mais la sortie de la commande *consume-output* à lieu dans la session d'origine (et non dans la nouvelle session qui vient d'être créée). D'une manière analogue, une commande *docker logs* afficherait ses informations uniquement dans la session d'origine.

```bash
docker exec $(docker ps | grep kafka | awk {'print $1'} | head -1) bash -c "/opt/scripts/consume-output.sh"
```

Pour se connecter au container kafka en exécution, il faut exécuter la suite d'instructions suivantes dans une nouvelle fenêtre de terminal:

```bash
docker exec -it $(docker ps | grep kafka | awk {'print $1'} | head -1) bash 
```

Les topics sont créés automatiquement lors du démarrage de l'application Java exécutée dans le container kstreams. La commande suivante pertmet de s'en assurer:

```bash
/opt/scripts/list-topics.sh
```

### Référentiel des usagers

Pour injecter des informations sur les usagers dans le topic `usagers`, il faut exécuter la commande suivante (dans le container kafka en exécution):

```bash
/opt/scripts/inject-usagers.sh
```

Cela injecte les messages (clé,valeur) suivants dans le référentiel :

```bash
1,{"id":1, "email":"usager.001@mail.com"}
2,{"id":2, "email":"usager.002@mail.com"}
3,{"id":3, "email":"usager.003@mail.com"}
```

### Demandes d'usagers

Pour injecter des demandes d'usagers dans le topic `demandes`, il faut exécuter la commande suivante (dans le container kafka en exécution):

```bash
/opt/scripts/inject-demandes.sh
```

Cela injecte les messages suivants dans le flux des demandes :

```bash
{"id": 1, "name": "usager", "firstName": "usr01"}
{"id": 2, "name": "usager", "firstName": "usr02"}
{"id": 30, "name": "usager", "firstName": "usr01"}
{"id": 1, "name": "usager", "firstName": "usr-01"}
```

Ici la clé n'est pas présente, ce qui revient à produire le message dans une partition aléatoire.

### Résultat

Pour consommer les messages présents dans le topic `demandes-enrichies`, il faut exécuter la commande suivante (dans le container kafka en exécution):

```bash
opt/scripts/consume-output.sh
```

Output :

```bash
{"id":2,"name":"produit2","price":13.40}
{"id":30,"name":"REF INCONNUE","price":1.05}
{"id":1,"name":"produit1","price":3.40}
{"id":1,"name":"produit1","price":3.45}
```

On voit que les achats ont été enrichis du libellé produit grâce au référentiel.

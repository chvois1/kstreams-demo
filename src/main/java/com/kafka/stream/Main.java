package com.kafka.stream;

import com.kafka.stream.model.Demande;
import com.kafka.stream.model.DemandeEnrichie;
import com.kafka.stream.model.Usager;
import com.kafka.stream.serializer.JsonDeserializer;
import com.kafka.stream.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Usage : Main <kafka-hosts> <zk-hosts>");
            System.exit(1);
        }

        String kafka = args[0];
        String zk = args[1];

        Properties streamsConfiguration = new Properties();
        // Donne un nom à l'application. Toutes les instances de cette application pourront se partager les partitions
        // de mêmes topics grâce à cet identifiant.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "enrichissement-demande");
        // Broker Kafka
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafka);
        // Noeud Zookeeper
        streamsConfiguration.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, zk);

        KStreamBuilder builder = new KStreamBuilder();

        // Initialisation des ser/déserialiseurs pour lire et écrire dans les topics
        Serde<Demande> DemandeSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Demande.class));
        Serde<DemandeEnrichie> DemandeEnrichiSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(DemandeEnrichie.class));
        Serde<Usager> usagerSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Usager.class));

        // Création d'un KStream (flux) à partir du topic "demandes"
        KStream<String, Demande> demandes = builder.stream(Serdes.String(), DemandeSerde, "demandes");

        // Création d'une KTable (table) à partir du topic "usagers"
        KTable<String, Usager> usagers = builder.table(Serdes.String(), usagerSerde, "usagers");

        KStream<String, DemandeEnrichie> enriched = demandes
                // Re-partitionnement du flux avec la nouvelle clé qui nous permettra de faire une jointure
                .map((k, v) -> new KeyValue<>(v.getId().toString(), v))
                // Copie du flux vers un nouveau topic avec la nouvelle clé
                .through(Serdes.String(), DemandeSerde, "demandes-by-usager-id")
                // Jointure du flux de demandes avec les usagers
                .leftJoin(usagers, (demande, ref) -> {
                    if (ref == null) return new DemandeEnrichie(demande.getId(), demande.getName(), demande.getFirstName(), "REF INCONNUE");
                    else return new DemandeEnrichie(demande.getId(), demande.getName(), demande.getFirstName(), ref.getEmail());
                });

        // On publie le flux dans un topic "demandes-enrichies"
        enriched.to(Serdes.String(), DemandeEnrichiSerde, "demandes-enrichies");

        // Enfin, on démarre l'application
        KafkaStreams streams = new KafkaStreams(builder, streamsConfiguration);
        streams.start();
    }

}



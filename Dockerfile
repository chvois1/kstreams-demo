FROM eclipse-temurin:17-jdk-jammy

RUN mkdir /opt/app
COPY target/com.kafka.stream-1.0-jar-with-dependencies.jar /opt/app/app.jar
CMD ["java", "-jar", "/opt/app/app.jar", "kafka:9092", "zk:2181"]

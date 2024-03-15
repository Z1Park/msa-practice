FROM openjdk:17-ea-11-slim

VOLUME /tmp
COPY apiEncryptionKey.jks apiEncryption.jks
COPY build/libs/config-service-0.0.1-SNAPSHOT.jar ConfigServer.jar

ENTRYPOINT ["java", "-jar", "ConfigServer.jar"]
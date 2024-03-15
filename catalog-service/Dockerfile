FROM openjdk:17-ea-11-slim

VOLUME /tmp
COPY build/libs/catalog-service-0.0.1-SNAPSHOT.jar CatalogService.jar

ENTRYPOINT ["java", "-jar", "CatalogService.jar"]
# Utiliser une image de base avec Maven et OpenJDK
FROM maven:3.9.9-amazoncorretto-23 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM openjdk:23-slim

WORKDIR /app

COPY --from=build /app/target/consumer-1.0-SNAPSHOT.jar /app/consumer.jar

COPY --from=build /app/src/main/resources/config.yaml /app/config.yaml


CMD ["java", "-jar", "/app/consumer.jar"]

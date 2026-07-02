# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd -r -u 1000 convive \
    && mkdir -p /app/uploads \
    && chown -R convive:convive /app

COPY --from=build /app/target/*.jar app.jar

USER convive
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

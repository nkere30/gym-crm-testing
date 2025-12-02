FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /project
COPY gym-crm-service/ .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /project/target/gym-crm-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

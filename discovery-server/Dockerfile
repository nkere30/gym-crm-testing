FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /project
COPY . .

WORKDIR /project/discovery-server
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /project/discovery-server/target/discovery-server-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]

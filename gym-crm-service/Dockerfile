FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /project
COPY gym-crm-service/ .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /project/target/gym-crm-service-1.0.0-SNAPSHOT.jar app.jar

# Disable integrations for the task
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
ENV SPRING_DATASOURCE_USERNAME=sa
ENV SPRING_DATASOURCE_PASSWORD=
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect

# Mongo disabled (must include database name!)
ENV SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/dummy

# JMS disabled
ENV SPRING_ACTIVEMQ_BROKER_URL=disabled
ENV SPRING_JMS_LISTENER_AUTO_STARTUP=false

# Eureka disabled
ENV EUREKA_CLIENT_ENABLED=false

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

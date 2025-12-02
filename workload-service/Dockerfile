FROM maven:3.9.6-eclipse-temurin-17 AS build

# Copy entire project
WORKDIR /project
COPY . .

# Build only workload-service module
WORKDIR /project/workload-service
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /project/workload-service/target/*.jar app.jar

# disabled integrations
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
ENV SPRING_DATASOURCE_USERNAME=sa
ENV SPRING_DATASOURCE_PASSWORD=
ENV SPRING_ACTIVEMQ_BROKER_URL=disabled
ENV SPRING_JMS_LISTENER_AUTO_STARTUP=false
ENV EUREKA_CLIENT_ENABLED=false

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

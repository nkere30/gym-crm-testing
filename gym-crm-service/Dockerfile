FROM maven:3.9.6-eclipse-temurin-17 AS build

# Copy entire project
WORKDIR /project
COPY . .

# Build only gym-crm-service module
WORKDIR /project/gym-crm-service
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /project/gym-crm-service/target/*.jar app.jar

# disable external integrations
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
ENV SPRING_DATASOURCE_USERNAME=sa
ENV SPRING_DATASOURCE_PASSWORD=
ENV SPRING_DATA_MONGODB_URI=disabled
ENV SPRING_ACTIVEMQ_BROKER_URL=disabled
ENV SPRING_JMS_LISTENER_AUTO_STARTUP=false
ENV EUREKA_CLIENT_ENABLED=false

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

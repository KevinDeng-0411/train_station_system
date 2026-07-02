FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Backend runtime with mysql client for backup/restore
FROM eclipse-temurin:21-jre
WORKDIR /app

# Install mysql client (for mysqldump/mysql restore)
RUN apt-get update && \
    apt-get install -y default-mysql-client && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/train-station-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
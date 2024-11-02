# To build image called mfa with version 1.0
# Run                   > docker build -t mfa:1.0 .
# Check image locally   > docker images --filter=reference='mfa:1.0'

# Build stage
FROM maven:3.9.6-amazoncorretto-21-debian AS build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:21-alpine3.20

COPY --from=build /target/*.jar app.jar

# Expose the port 8080 that the application listens on
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
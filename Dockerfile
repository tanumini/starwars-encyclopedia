
# Use official base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file (after building the project) into the container
COPY target/starwars-api-0.0.1-SNAPSHOT.jar starwars-api.jar

# Expose the application port (default port for Spring Boot is 8080)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "starwars-api-0.0.1-SNAPSHOT.jar"]

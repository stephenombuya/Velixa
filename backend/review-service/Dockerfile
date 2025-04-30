# Use the official OpenJDK image as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file from the host into the container at /app
COPY target/order-service-0.0.1-SNAPSHOT.jar inventory-service.jar

# Expose port 8082 to the outside world
EXPOSE 8084

# Run the application
ENTRYPOINT ["java", "-jar", "review-service.jar"]

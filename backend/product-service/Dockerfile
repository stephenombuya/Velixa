# Use a lightweight base image with Java 17 or 21 (whichever you're using)
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the jar file into the container
COPY target/product-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8082

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]

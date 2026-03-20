# Use lightweight Java 21 image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/etms-api-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render uses dynamic PORT)
EXPOSE 8080

# Run app with dynamic port support
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
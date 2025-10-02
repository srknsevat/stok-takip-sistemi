# Ultra Simple Dockerfile for Railway
FROM maven:3.9.4-openjdk-17-slim

# Set working directory
WORKDIR /app

# Copy everything
COPY . .

# Build and run in one step
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-jar", "target/*.jar"]

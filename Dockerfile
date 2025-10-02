# Ultra Simple Dockerfile for Railway
FROM openjdk:17-jdk-slim

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy everything
COPY . .

# Build application (skip tests for faster build)
RUN mvn clean package -DskipTests -q

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-jar", "target/*.jar"]

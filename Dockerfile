# Use an official OpenJDK 21 runtime as base image
FROM openjdk:21-jdk-slim
LABEL authors="NnyabeChamamme"


LABEL authors="NnyabeChamamme"

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the target directory to the container
COPY target/book-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
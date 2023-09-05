# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the packaged JAR file into the container at the working directory
COPY target/aws-ip-range-service-0.0.1-SNAPSHOT.jar .

# Specify the command to run your application
CMD ["java", "-jar", "aws-ip-range-service-0.0.1-SNAPSHOT.jar"]
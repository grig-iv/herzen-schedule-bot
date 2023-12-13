# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file into the container at /usr/src/app
COPY ./target/uberjar/schedule-bot-0.1.0-SNAPSHOT-standalone.jar ./app.jar

# Run the jar file
CMD ["java", "-jar", "./app.jar"]

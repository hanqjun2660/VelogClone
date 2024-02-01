# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

LABEL image.name="clog"

# Set the working directory to /app
WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle gradle

# Copy the project files
COPY build.gradle .
COPY settings.gradle .

# Download dependencies and build the project
RUN ./gradlew build

# Copy the compiled JAR file
COPY build/libs/clog.jar /app/

# Expose port 9090 to the outside world
EXPOSE 9091

# Specify the command to run on container start
CMD ["java", "-jar", "clog.jar"]

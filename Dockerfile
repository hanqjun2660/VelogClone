# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory to /app
WORKDIR /app

# Copy the compiled JAR file from the build stage
COPY build/libs/clog.jar /app/

# Expose port 8080 to the outside world
EXPOSE 9090

# Specify the command to run on container start
CMD ["java", "-jar", "clog.jar"]

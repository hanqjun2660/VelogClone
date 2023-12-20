# Use the locally built OpenJDK 17 image
FROM adoptopenjdk:17-jre-hotspot

# Set the working directory to /app
WORKDIR /app

# Copy the compiled JAR file from the build stage
COPY build/libs/clog.jar /app/

# Expose port 9090 to the outside world
EXPOSE 9090

# Specify the command to run on container start
CMD ["java", "-jar", "clog.jar"]

# Use a Java 17 runtime (matches your Spring Boot project)
FROM eclipse-temurin:17-jdk

# Workdir inside the container
WORKDIR /app

# Copy the built jar into the image
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose backend port
EXPOSE 8080

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

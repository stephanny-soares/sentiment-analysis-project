FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src
COPY resources ./src/main/resources

# Build the application
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests && \
    apt-get remove -y maven && apt-get autoremove -y && apt-get clean

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/sentiment-api-0.0.1-SNAPSHOT.jar"]
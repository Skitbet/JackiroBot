# Use OpenJDK 17 base image
FROM eclipse-temurin:17-jdk

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline

COPY src ./src

# Package the application
RUN mvn package

# Run the bot
CMD ["java", "-jar", "target/Jackiro-jar-with-dependencies.jar"]

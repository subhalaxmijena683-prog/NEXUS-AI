FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

EXPOSE 8081

CMD ["java", "-jar", "target/nexus-ai-backend-0.0.1-SNAPSHOT.jar"]

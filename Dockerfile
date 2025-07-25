FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY pom.xml .
COPY . .
RUN ./mvnw clean install -DskipTests
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/assembleia-0.0.1-SNAPSHOT.jar ./assembleia.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/assembleia.jar"]

# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
RUN mkdir -p /app
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run Stage
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/travel-budget-app.jar travel-budget-app.jar
ENTRYPOINT ["java", "-jar", "travel-budget-app.jar"]
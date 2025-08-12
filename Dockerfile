# Stage 1: Build
FROM maven:3.9.9-amazoncorretto-21 AS build
LABEL authors="CanhPC"

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests -Dspotless.check.skip=true

# Stage 2: Run
FROM amazoncorretto:21.0.7

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

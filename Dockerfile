FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY src src
RUN mvn package spring-boot:repackage -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /workspace/app/target/gateway-service.jar /app
CMD ["java","-jar", "/app/gateway-service.jar"]
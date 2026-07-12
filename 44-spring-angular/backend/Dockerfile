# Modulo 44 - Backend Dockerfile (multi-stage).
# Basado en el patron del modulo 26.

# Stage 1: build con JDK + Maven
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn -B clean package -DskipTests

# Stage 2: runtime con solo JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/spring-angular-1.0.0.jar app.jar
EXPOSE 8080
RUN addgroup -S spring && adduser -S spring -G spring
USER spring
ENTRYPOINT ["java", "-jar", "app.jar"]

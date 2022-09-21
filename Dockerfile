# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY gradle ./gradle
COPY src ./src
COPY build.gradle settings.gradle gradlew ./
RUN chmod +x ./gradlew
RUN ./gradlew shadowJar
CMD ["java", "-jar", "build/libs/RCCAssistance-1.0-all.jar"]
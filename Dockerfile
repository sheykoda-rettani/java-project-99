FROM gradle:9.4.1-jdk21 AS builder

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

COPY gradle gradle
COPY gradle.properties gradle.properties
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts
COPY src src

RUN gradle clean assemble --no-daemon

FROM eclipse-temurin:21-jre

COPY --from=builder /app/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
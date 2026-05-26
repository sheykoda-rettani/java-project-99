# syntax = docker/dockerfile:1.2
FROM eclipse-temurin:25

RUN apt-get update && apt-get install -yq make unzip

COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY config config
COPY package.json .
COPY package-lock.json .

COPY src src


RUN --mount=type=secret,id=RSA_PRIVATE_KEY_PROD,dst=/etc/secrets/RSA_PRIVATE_KEY_PROD \
    ./gradlew --no-daemon build -x test

EXPOSE 8080

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar
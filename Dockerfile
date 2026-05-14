# syntax = docker/dockerfile:1.2

FROM eclipse-temurin:21-jdk
ARG GRADLE_VERSION=9.4.1

RUN apt-get update && apt-get install -yq make unzip

RUN wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip \
    && rm gradle-${GRADLE_VERSION}-bin.zip

ENV GRADLE_HOME=/opt/gradle

RUN mv gradle-${GRADLE_VERSION} ${GRADLE_HOME}

ENV PATH=$PATH:$GRADLE_HOME/bin

COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY config config
COPY package.json .
COPY package-lock.json .

COPY src src

RUN --mount=type=secret,id=rsa_private_key_prod,dst=/etc/secrets/rsa_private_key_prod \
    gradle --no-daemon build -x test && \
    if [ -f "/etc/secrets/rsa_private_key_prod" ]; then \
        echo "--- DOCKER BUILD LOG ---"; \
        echo "Секретный файл найден в контейнере сборки."; \
        cp /etc/secrets/rsa_private_key_prod /app/src/main/resources/certs/private.pem; \
    else \
        echo "--- DOCKER BUILD LOG ---"; \
        echo "ОШИБКА: Секретный файл НЕ найден. Сборка продолжится, но ключ не будет встроен."; \
    fi

RUN gradle --no-daemon build -x test

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=50.0"
EXPOSE 8080

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar
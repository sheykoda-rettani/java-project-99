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

RUN gradle --no-daemon dependencies
COPY src src

RUN gradle --no-daemon build

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=50.0"
EXPOSE 8080

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar
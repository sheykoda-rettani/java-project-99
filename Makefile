run: build run-dist

build:
	 ./gradlew --no-daemon build

run-dist:
	 java -jar build\libs\app-0.0.1-SNAPSHOT.jar

.PHONY: build
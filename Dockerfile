# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy@sha256:73ed442231ec66cfe48cae0b263ce619a75ab96d162ab325f38fc0a269ee3aab AS build
WORKDIR /workspace

# Maven installs the Node/npm versions pinned in pom.xml and builds the Vue UI.
ENV HUSKY=0
COPY . .
RUN sed -i 's/\r$//' mvnw .mvn/wrapper/maven-wrapper.properties && chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw --batch-mode --no-transfer-progress -Pprod clean verify -DskipTests=true
RUN cp target/semprotdb-*.jar /workspace/app.jar

FROM eclipse-temurin:17-jre-jammy@sha256:b3054a20e1a851574cdb1692dd0db5310c647e62214a2a7abecbec1b9e3ae8f1 AS runtime
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 10001 semprotdb \
    && useradd --uid 10001 --gid semprotdb --home-dir /app --no-create-home semprotdb \
    && mkdir -p /app/data \
    && chown -R semprotdb:semprotdb /app

WORKDIR /app
COPY --from=build --chown=semprotdb:semprotdb /workspace/app.jar ./app.jar
ENV SPRING_PROFILES_ACTIVE=prod,docker SERVER_PORT=3001 SEMPROTDB_FILES_PATH=/app/data
USER semprotdb
EXPOSE 3001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

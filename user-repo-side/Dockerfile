FROM docker/java-jdk:11
COPY . /app/
WORKDIR /app

RUN ./gradlew test --no-daemon
RUN ./gradlew bootJar --no-daemon
RUN ./gradlew test jacocoTestReport --no-daemon
RUN mv build/libs/*jar ./hello-world.jar

# establish app dynamics environment variables
ENV APPDYNAMICS_AGENT_ACCOUNT_NAME=sirket-dev
ENV APPDYNAMICS_AGENT_APP_NAME=hello-world
ENV APPDYNAMICS_AGENT_TIER_NAME=dev-hello-world
ENV APPDYNAMICS_AGENT_NODE_PREFIX=hello-world

ARG VERSION
ARG JAVA_OPTS
ENV VERSION ${VERSION}
ENV JAVA_OPTS ${JAVA_OPTS}

ENTRYPOINT java ${JAVA_OPTS} $(enable_appdynamics.sh) -jar "hello-world.jar"
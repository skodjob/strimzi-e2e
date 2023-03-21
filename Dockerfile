FROM quay.io/rh_integration/strimzi-tools:latest

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk

USER root

COPY . /opt/thor/

RUN cd /opt/thor && \
    mvn clean install -DskipTests
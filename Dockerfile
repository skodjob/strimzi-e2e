FROM quay.io/tealc/apophis:latest

COPY . /opt/thor/

RUN cd /opt/thor && \
    mvn clean install -DskipTests
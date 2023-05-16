/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.worker01.status;

import io.strimzi.api.kafka.model.KafkaConnector;
import io.tealc.Abstract;
import io.tealc.ClusterManager;
import io.tealc.EClusters;
import io.tealc.ENamespaces;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConnectorsStatusTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorsStatusTest.class);

    @ParameterizedTest(name = "{0}-kafkaConnector-in-namespace-{1}")
    @MethodSource("getConnectorNames")
    void testKafkaConnectorIsReady(String namespace, String connectorName) {
        KafkaConnector kafkaConnector = ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaConnectorClient().inNamespace(namespace).withName(connectorName).get();
        String status = kafkaConnector.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).collect(Collectors.toList()).get(0).getStatus();
        LOGGER.debug("KafkaConnector: {}", kafkaConnector);
        assertThat(String.format("KafkaConnector %s is not ready in namespace %s", kafkaConnector.getMetadata().getName(), namespace), status, is("True"));
    }

    private Stream<Arguments> getConnectorNames() {
        return Stream.of(
                Arguments.of(ENamespaces.CONNECT.name, "devconf-telegram-connector-sink"),
                Arguments.of(ENamespaces.CONNECT.name, "devconf-twitter-search"),
                Arguments.of(ENamespaces.CONNECT.name, "kafka-telegram-connector-sink"),
                Arguments.of(ENamespaces.CONNECT.name, "kafka-twitter-search"),
                Arguments.of(ENamespaces.CONNECT.name, "stargate-telegram-connector-sink"),
                Arguments.of(ENamespaces.CONNECT.name, "stargate-twitter-search"),
                Arguments.of(ENamespaces.CONNECT.name, "strimzi-telegram-connector-sink"),
                Arguments.of(ENamespaces.CONNECT.name, "strimzi-twitter-search"),
                Arguments.of(ENamespaces.CONNECT.name, "imhotep-mysql"),
                Arguments.of(ENamespaces.CONNECT.name, "imhotep-postgres"),
                Arguments.of(ENamespaces.CONNECT.name, "mongodb-connector")
           );
    }
}

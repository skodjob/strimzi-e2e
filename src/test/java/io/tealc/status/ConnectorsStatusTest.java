/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.status;

import io.strimzi.api.kafka.model.KafkaConnector;
import io.tealc.Abstract;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConnectorsStatusTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorsStatusTest.class);

    @ParameterizedTest(name = "testKafkaConnectorIsReady - {0}")
    @MethodSource("getConnectorNames")
    void testKafkaConnectorIsReady(String connectorName) {
        KafkaConnector kafkaConnector = getClient().kafkaConnectorClient().inNamespace(TWITTER_NAMESPACE).withName(connectorName).get();
        String status = kafkaConnector.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).collect(Collectors.toList()).get(0).getStatus();
        LOGGER.debug("KafkaConnector: {}", kafkaConnector);
        assertThat("KafkaConnector is not ready in namespace " + TWITTER_NAMESPACE, status, is("True"));
    }

    private Stream<String> getConnectorNames() {
        return Stream.of("devconf-telegram-connector-sink",
                "devconf-twitter-search",
                "kafka-telegram-connector-sink",
                "kafka-twitter-search",
                "stargate-telegram-connector-sink",
                "stargate-twitter-search",
                "strimzi-telegram-connector-sink",
                "strimzi-twitter-search");
    }
}

/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.status;

import io.strimzi.api.kafka.model.Kafka;
import io.strimzi.api.kafka.model.KafkaConnect;
import io.tealc.Abstract;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ComponentsStatusTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentsStatusTest.class);

    @Test
    void testKafkaIsReady() {
        Kafka kafka = getClient().kafkaClient().inNamespace(KAFKA_NAMESPACE).withName("anubis").get();
        String status = kafka.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).collect(Collectors.toList()).get(0).getStatus();
        LOGGER.debug("Kafka: {}", kafka);
        assertThat("Kafka is not ready in namespace " + KAFKA_NAMESPACE, status, is("True"));
    }

    @Test
    void testKafkaConnectIsReady() {
        KafkaConnect kafkaConnect = getClient().kafkaConnectClient().inNamespace(TWITTER_NAMESPACE).withName("hathor").get();
        String status = kafkaConnect.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).collect(Collectors.toList()).get(0).getStatus();
        LOGGER.debug("KafkaConnect: {}", kafkaConnect);
        assertThat("KafkaConnect is not ready in namespace " + TWITTER_NAMESPACE, status, is("True"));
    }
}

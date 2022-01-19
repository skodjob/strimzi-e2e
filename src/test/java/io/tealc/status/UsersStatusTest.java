/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.status;

import io.strimzi.api.kafka.model.KafkaUser;
import io.tealc.Abstract;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UsersStatusTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersStatusTest.class);

    @ParameterizedTest(name = "testKafkaUserIsReady - {0}")
    @MethodSource("getUserNames")
    void testKafkaUserIsReady(String userName) {
        KafkaUser kafkaUser = getClient().kafkaUserClient().inNamespace(KAFKA_NAMESPACE).withName(userName).get();
        String status = kafkaUser.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).collect(Collectors.toList()).get(0).getStatus();
        LOGGER.debug("KafkaUser: {}", kafkaUser);
        assertThat("KafkaUser is not ready in namespace " + KAFKA_NAMESPACE, status, is("True"));
    }

    private Stream<String> getUserNames() {
        return Stream.of("canary",
                "hathor",
                "her-ur",
                "kafka-external-consumer",
                "kafka-external-producer",
                "kafka-external-streams",
                "kafka-internal-consumer",
                "kafka-internal-producer",
                "kafka-internal-streams");
    }
}

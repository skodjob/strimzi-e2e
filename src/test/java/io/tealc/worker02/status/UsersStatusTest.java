/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.worker02.status;

import io.strimzi.api.kafka.model.KafkaUser;
import io.tealc.Abstract;
import io.tealc.ClusterManager;
import io.tealc.EClusters;
import io.tealc.ENamespaces;
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
        KafkaUser kafkaUser = ClusterManager.getInstance().getClient(EClusters.WORKER_02).kafkaUserClient().inNamespace(ENamespaces.KAFKA.name).withName(userName).get();
        String status = kafkaUser.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).toList().get(0).getStatus();
        LOGGER.debug("KafkaUser: {}", kafkaUser);
        assertThat(String.format("KafkaUser %s is not ready in namespace %s", kafkaUser.getMetadata().getName(), ENamespaces.KAFKA.name), status, is("True"));
    }

    private Stream<String> getUserNames() {
        return Stream.of("superuser-heimdall");
    }
}

/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.worker02.status;

import io.strimzi.api.kafka.model.Kafka;
import io.strimzi.api.kafka.model.KafkaConnect;
import io.strimzi.api.kafka.model.KafkaMirrorMaker2;
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

public class ComponentsStatusTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentsStatusTest.class);

    @ParameterizedTest(name = "{1}-in-{0}-higher-than-{2}")
    @MethodSource("kafkaInstances")
    void testKafkaIsReady(String namespace, String name) {
        Kafka kafka = ClusterManager.getInstance().getClient(EClusters.WORKER_02).kafkaClient().inNamespace(namespace).withName(name).get();
        String status = kafka.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).toList().get(0).getStatus();
        LOGGER.debug("Kafka: {}", kafka);
        assertThat(String.format("Kafka %s is not ready in namespace %s", name, namespace), status, is("True"));
    }

    private static Stream<Arguments> kafkaInstances() {
        return Stream.of(
                Arguments.of(ENamespaces.KAFKA.name, "heimdall")
        );
    }
}

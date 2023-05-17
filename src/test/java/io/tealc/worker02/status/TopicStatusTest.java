/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.worker02.status;

import io.strimzi.api.kafka.model.KafkaTopic;
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

public class TopicStatusTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicStatusTest.class);

    @ParameterizedTest(name = "testKafkaTopicIsReady - {0}")
    @MethodSource("getTopicNames")
    void testKafkaUserIsReady(String topicName) {
        KafkaTopic kafkaTopic = ClusterManager.getInstance().getClient(EClusters.WORKER_02).kafkaTopicClient().inNamespace(ENamespaces.KAFKA.name).withName(topicName).get();
        String status = kafkaTopic.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).toList().get(0).getStatus();
        LOGGER.debug("KafkaTopic: {}", kafkaTopic);
        assertThat(String.format("KafkaTopic %s is not ready in namespace %s", kafkaTopic.getMetadata().getName(), ENamespaces.KAFKA.name), status, is("True"));
    }

    private Stream<String> getTopicNames() {
        return ClusterManager.getInstance().getClient(EClusters.WORKER_02).kafkaTopicClient().inNamespace(ENamespaces.KAFKA.name).list().getItems().stream().map(name -> name.getMetadata().getName()).toList().stream();
    }
}

/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.smoke;

import io.tealc.Abstract;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class ClientsSmokeTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientsSmokeTest.class);

    @Test
    void testKafkaExternalClientIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(CLIENTS_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("kafka-external"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("Kafka clients pod names: {}", listPodNames);
        assertThat("There are not enough Kafka pods in namespace " + CLIENTS_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(3));
    }

    @Test
    void testKafkaInternalClientIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(CLIENTS_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("kafka-internal"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("KafkaClients pod names: {}", listPodNames);
        assertThat("There are not Kafka clients enough Kafka pods in namespace " + CLIENTS_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(3));
    }
}

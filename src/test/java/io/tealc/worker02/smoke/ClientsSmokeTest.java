/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.worker02.smoke;

import io.fabric8.kubernetes.api.model.Pod;
import io.tealc.Abstract;
import io.tealc.ClusterManager;
import io.tealc.EClusters;
import io.tealc.ENamespaces;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

public class ClientsSmokeTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientsSmokeTest.class);

    @ParameterizedTest(name = "{1}-in-{0}-higher-than-{2}")
    @MethodSource("clientDeployments")
    void testKafkaClientsAreDeployed(String namespace, String clientNamePrefix, int count) {
        List<Pod> listPods = ClusterManager.getInstance().getClient(EClusters.WORKER_02).inNamespace(namespace).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains(clientNamePrefix)
                        && !(pod.getStatus().getReason() != null && pod.getStatus().getReason().contains("Evicted"))).toList();
        LOGGER.info("Kafka clients pods list size: {}", listPods.size());
        assertThat("There are not enough KafkaClients pods in namespace " + namespace, listPods.size(), greaterThanOrEqualTo(count));

        for (Pod pod : listPods) {
            String podPhase = pod.getStatus().getPhase();
            assertThat(String.format("Pod {} is not running ({})", pod.getMetadata().getName(), podPhase),
                    podPhase, is("Running"));
        }
    }

    private static Stream<Arguments> clientDeployments() {
        return Stream.of(
                Arguments.of(ENamespaces.CLIENTS.name, "kafka-internal-consumer", 3)
        );
    }
}

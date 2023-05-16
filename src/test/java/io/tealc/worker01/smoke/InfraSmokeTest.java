/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.worker01.smoke;

import io.fabric8.kubernetes.api.model.Pod;
import io.tealc.Abstract;
import io.tealc.ClusterManager;
import io.tealc.EClusters;
import io.tealc.ENamespaces;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class InfraSmokeTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfraSmokeTest.class);

    @ParameterizedTest(name = "{1}-in-{0}-higher-than-{2}")
    @MethodSource("componentDeployments")
    void testComponentIsDeployed(String namespace, String podPrefix, int count) {
        List<Pod> listPods = ClusterManager.getInstance().getClient(EClusters.WORKER_01).inNamespace(namespace).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains(podPrefix)).toList();
        LOGGER.info("Kafka clients pods list size: {}", listPods.size());
        assertThat("There are not enough KafkaClients pods in namespace " + namespace, listPods.size(), greaterThanOrEqualTo(count));

        for (Pod pod: listPods) {
            String podPhase = pod.getStatus().getPhase();
            if (pod.getMetadata().getName().contains("build")) {
                assertThat(String.format("Pod %s is not Succeeded (%s)", pod.getMetadata().getName(), podPhase),
                        podPhase, Matchers.is("Succeeded"));
            } else {
                assertThat(String.format("Pod %s is not running (%s)", pod.getMetadata().getName(), podPhase),
                        podPhase, Matchers.is("Running"));
            }

        }
    }

    private static Stream<Arguments> componentDeployments() {
        return Stream.of(
                Arguments.of(ENamespaces.KAFKA.name, "kafka", 7),
                Arguments.of(ENamespaces.KAFKA_KRAFT.name, "kafka", 5),
                Arguments.of(ENamespaces.OPERATOR.name, "strimzi-cluster-operator", 1),
                Arguments.of(ENamespaces.OPERATOR_KRAFT.name, "strimzi-cluster-operator", 1),
                Arguments.of(ENamespaces.KAFKA.name, "kafka-exporter", 1),
                Arguments.of(ENamespaces.KAFKA_KRAFT.name, "kafka-exporter", 1),
                Arguments.of(ENamespaces.KAFKA.name, "cruise-control", 1),
                Arguments.of(ENamespaces.KAFKA_KRAFT.name, "cruise-control", 1),
                Arguments.of(ENamespaces.KAFKA.name, "entity-operator", 1),
                Arguments.of(ENamespaces.CONNECT.name, "hathor", 3),
                Arguments.of(ENamespaces.CONNECT.name, "imhotep", 1),
                Arguments.of(ENamespaces.MIRROR_MAKER.name, "replicator-carter", 3),
                Arguments.of(ENamespaces.MIRROR_MAKER_KRAFT.name, "replicator-set", 3),
                Arguments.of(ENamespaces.DRAIN_CLEANER.name, "drain-cleaner", 1)
        );
    }
}

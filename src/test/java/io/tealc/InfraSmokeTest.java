/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class InfraSmokeTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfraSmokeTest.class);

    @Test
    void testKafkaIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(KAFKA_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("kafka"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("Kafka pod names: {}", listPodNames);
        assertThat("There are no pods in namespace " + KAFKA_NAMESPACE, listPodNames, is(not(empty())));
        assertThat("There are not enough Kafka pods in namespace " + STRIMZI_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(6));
    }

    @Test
    void testStrimziIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(STRIMZI_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("strimzi-cluster-operator"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("Strimzi CO pod names: {}", listPodNames);
        assertThat("There is no strimzi operator pod in namespace " + STRIMZI_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(1));
    }

    @Test
    void testKafkaExporterIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(KAFKA_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("kafka-exporter"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("KafkaExporter pod names: {}", listPodNames);
        assertThat("There are not enough KafkaExporter pods in namespace " + STRIMZI_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(1));
    }

    @Test
    void testKafkaCruiseControlIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(KAFKA_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("cruise-control"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("CruiseControl pod names: {}", listPodNames);
        assertThat("There are not enough CruiseControl pods in namespace " + STRIMZI_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(1));
    }

    @Test
    void testKafkaEntityOperatorIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(KAFKA_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("entity-operator"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("EntityOperator pod names: {}", listPodNames);
        assertThat("There are not enough EntityOperator pods in namespace " + STRIMZI_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(1));
    }

    @Test
    void testKafkaConnectIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(TWITTER_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("hathor-connect") && !pod.getMetadata().getName().contains("build"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("KafkaConnect pod names: {}", listPodNames);
        assertThat("There are not enough KafkaConnect pods in namespace " + TWITTER_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(1));
    }
}

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

public class SmokeTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmokeTest.class);

    @Test
    void testKafkaIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(KAFKA_NAMESPACE).listPods().stream().map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("Pod names: {}", listPodNames);
        assertThat("There are no pods in namespace " + KAFKA_NAMESPACE, listPodNames, is(not(empty())));
        assertThat("There are not enough Kafka pods in namespace " + STRIMZI_NAMESPACE, (int) listPodNames.stream().filter(name -> name.contains("kafka")).count(), greaterThanOrEqualTo(6));
    }

    @Test
    void testStrimziIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(STRIMZI_NAMESPACE).listPods().stream().map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("Pod names: {}", listPodNames);
        assertThat("There are no pods in namespace " + KAFKA_NAMESPACE, listPodNames, is(not(empty())));
        assertThat("There is no strimzi operator pod in namespace " + STRIMZI_NAMESPACE, (int) listPodNames.stream().filter(name -> name.contains("strimzi-cluster-operator")).count(), is(1));
    }
}

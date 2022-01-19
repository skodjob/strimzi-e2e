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

public class TwitterConnectorsSmokeTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterConnectorsSmokeTest.class);

    @Test
    void testDevconfTwitterParserIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(TWITTER_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("devconf-twitter-parser"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("Defconf Twitter Parser pod names: {}", listPodNames);
        assertThat("There are not Defconf Twitter Parser enough Kafka pods in namespace " + TWITTER_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(1));
    }

    @Test
    void testKafkaTwitterParserIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(TWITTER_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("kafka-twitter-parser"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("Kafka Twitter Parser pod names: {}", listPodNames);
        assertThat("There are not Defconf Twitter Parser enough Kafka pods in namespace " + TWITTER_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(1));
    }

    @Test
    void testStrimziTwitterParserIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(TWITTER_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("strimzi-twitter-parser"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("Strimzi Twitter Parser pod names: {}", listPodNames);
        assertThat("There are not Defconf Twitter Parser enough Kafka pods in namespace " + TWITTER_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(1));
    }

    @Test
    void testStargateTwitterParserIsDeployed() {
        List<String> listPodNames = getClient().inNamespace(TWITTER_NAMESPACE).listPods().stream()
                .filter(pod -> pod.getMetadata().getName().contains("stargate-twitter-parser"))
                .map(pod -> pod.getMetadata().getName()).collect(Collectors.toList());
        LOGGER.debug("Stargate Twitter Parser pod names: {}", listPodNames);
        assertThat("There are not Defconf Twitter Parser enough Kafka pods in namespace " + TWITTER_NAMESPACE, listPodNames.size(), greaterThanOrEqualTo(1));
    }
}

/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.worker01.status;

import io.strimzi.api.kafka.model.Kafka;
import io.strimzi.api.kafka.model.KafkaConnect;
import io.strimzi.api.kafka.model.KafkaMirrorMaker2;
import io.strimzi.api.kafka.model.KafkaRebalance;
import io.strimzi.api.kafka.model.StrimziPodSet;
import io.strimzi.api.kafka.model.nodepool.KafkaNodePool;
import io.tealc.Abstract;
import io.tealc.ClusterManager;
import io.tealc.EClusters;
import io.tealc.ENamespaces;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class ComponentsStatusTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentsStatusTest.class);

    @ParameterizedTest(name = "{1}-in-{0}-higher-than-{2}")
    @MethodSource("kafkaInstances")
    void testKafkaIsReady(String namespace, String name) {
        Kafka kafka = ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaClient().inNamespace(namespace).withName(name).get();
        String status = kafka.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).toList().get(0).getStatus();
        LOGGER.debug("Kafka: {}", kafka);
        assertThat(String.format("Kafka %s is not ready in namespace %s", name, namespace), status, is("True"));
    }

    @ParameterizedTest(name = "{1}-in-{0}-higher-than-{2}")
    @MethodSource("kafkaConnectInstances")
    void testKafkaConnectIsReady(String namespace, String name) {
        KafkaConnect kafkaConnect = ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaConnectClient().inNamespace(namespace).withName(name).get();
        String status = kafkaConnect.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).toList().get(0).getStatus();
        LOGGER.debug("KafkaConnect: {}", kafkaConnect);
        assertThat(String.format("KafkaConnect %s is not ready in namespace %s", name, namespace), status, is("True"));
    }

    @ParameterizedTest(name = "{1}-in-{0}-higher-than-{2}")
    @MethodSource("kafkaMirrorMakerInstances")
    void testKafkaMirrorMaker2IsReady(String namespace, String name) {
        KafkaMirrorMaker2 kafkaMirrorMaker2 = ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaMirrorMaker2Client().inNamespace(namespace).withName(name).get();
        String status = kafkaMirrorMaker2.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).toList().get(0).getStatus();
        LOGGER.debug("KafkaMirrorMaker2: {}", kafkaMirrorMaker2);
        assertThat(String.format("KafkaMirrorMaker2 %s is not ready in namespace %s", name, namespace), status, is("True"));
    }

    @ParameterizedTest(name = "{1}-in-{0}-higher-than-{2}")
    @MethodSource("kafkaRebalanceInstances")
    void testKafkaRebalance2IsReady(String namespace, String name) {
        KafkaRebalance kafkaRebalance = ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaRebalanceClient().inNamespace(namespace).withName(name).get();
        String status = kafkaRebalance.getStatus().getConditions().stream().filter(item -> item.getType().equals("Ready")).toList().get(0).getStatus();
        LOGGER.debug("KafkaRebalance: {}", kafkaRebalance);
        assertThat(String.format("KafkaRebalance %s is not ready in namespace %s", name, namespace), status, is("True"));
    }

    @ParameterizedTest(name = "{1}-in-{0}-higher-than-{2}")
    @MethodSource("strimziPodSetInstances")
    void testStrimziPodSetIsReady(String namespace, String name) {
        StrimziPodSet strimziPodSet = ClusterManager.getInstance().getClient(EClusters.WORKER_01).strimziPodSetClient().inNamespace(namespace).withName(name).get();
        LOGGER.debug("StrimziPodSet: {}", strimziPodSet);
        assertThat(String.format("StrimziPodSet %s pods status has 0 value!", name, namespace), strimziPodSet.getStatus().getPods(), is(greaterThan(0)));
        assertThat(String.format("StrimziPodSet %s readyPods status has 0 value!", name, namespace), strimziPodSet.getStatus().getReadyPods(), is(greaterThan(0)));
        assertThat(String.format("StrimziPodSet %s currentPods status has 0 value!", name, namespace), strimziPodSet.getStatus().getCurrentPods(), is(greaterThan(0)));
        assertThat(String.format("StrimziPodSet %s observerGeneration status has 0 value!", name, namespace), strimziPodSet.getStatus().getObservedGeneration(), is(greaterThan(0L)));
    }

    @ParameterizedTest(name = "{1}-in-{0}-higher-than-{2}")
    @MethodSource("kafkaNodePoolsInstances")
    void testKafkaNodePoolIsReady(String namespace, String name) {
        KafkaNodePool kafkaNodePool = ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaNodePoolClient().inNamespace(namespace).withName(name).get();
        LOGGER.debug("KafkaNodePool: {}", kafkaNodePool);
        assertThat(String.format("KafkaNodePool %s replicas status has 0 value!", name, namespace), kafkaNodePool.getStatus().getReplicas(), is(greaterThan(0)));
        assertThat(String.format("KafkaNodePool %s clusterId status has 0 value!", name, namespace), kafkaNodePool.getStatus().getClusterId(), is(notNullValue()));
        assertThat(String.format("KafkaNodePool %s NodeIDs status has 0 value!", name, namespace), kafkaNodePool.getStatus().getNodeIds(), is(notNullValue()));
    }

    private static Stream<Arguments> kafkaInstances() {
        return Stream.of(
                Arguments.of(ENamespaces.KAFKA.name, "anubis"),
                Arguments.of(ENamespaces.KAFKA_KRAFT.name, "horus")
        );
    }

    private static Stream<Arguments> kafkaConnectInstances() {
        return Stream.of(
                Arguments.of(ENamespaces.CONNECT.name, "imhotep")
        );
    }

    private static Stream<Arguments> kafkaMirrorMakerInstances() {
        return Stream.of(
                Arguments.of(ENamespaces.MIRROR_MAKER.name, "replicator-carter"),
                Arguments.of(ENamespaces.MIRROR_MAKER_KRAFT.name, "replicator-set")
        );
    }

    private static Stream<Arguments> kafkaRebalanceInstances() {
        return Stream.of(
                Arguments.of(ENamespaces.KAFKA.name, "anubis-rebalance"),
                Arguments.of(ENamespaces.KAFKA_KRAFT.name, "horus-rebalance")
        );
    }

    private static Stream<Arguments> strimziPodSetInstances() {
        return  Stream.of(
                Arguments.of(ENamespaces.KAFKA.name, "anubis-pool-big"),
                Arguments.of(ENamespaces.KAFKA.name, "anubis-pool-small"),
                Arguments.of(ENamespaces.KAFKA.name, "anubis-zookeeper"),
                Arguments.of(ENamespaces.KAFKA_KRAFT.name, "horus-broker"),
                Arguments.of(ENamespaces.KAFKA_KRAFT.name, "horus-controller"),
                Arguments.of(ENamespaces.CONNECT.name, "hathor-connect"),
                Arguments.of(ENamespaces.CONNECT.name, "imhotep-connect"),
                Arguments.of(ENamespaces.MIRROR_MAKER.name, "replicator-carter-mirrormaker2"),
                Arguments.of(ENamespaces.MIRROR_MAKER_KRAFT.name, "replicator-set-mirrormaker2")
        );
    }

    private static Stream<Arguments> kafkaNodePoolsInstances() {
        return  Stream.of(
                Arguments.of(ENamespaces.KAFKA.name, "pool-big"),
                Arguments.of(ENamespaces.KAFKA.name, "pool-small"),
                Arguments.of(ENamespaces.KAFKA_KRAFT.name, "broker"),
                Arguments.of(ENamespaces.KAFKA_KRAFT.name, "controller")
        );
    }
}

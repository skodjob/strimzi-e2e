/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.worker01.kafka;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.strimzi.api.kafka.model.Kafka;
import io.strimzi.api.kafka.model.nodepool.KafkaNodePool;
import io.strimzi.api.kafka.model.nodepool.ProcessRoles;
import io.strimzi.api.kafka.model.status.KafkaStatus;
import io.tealc.Abstract;
import io.tealc.ClusterManager;
import io.tealc.EClusters;
import io.tealc.Utils;
import io.tealc.templates.ConfigMapTemplates;
import io.tealc.templates.KafkaNodePoolTemplates;
import io.tealc.templates.KafkaTemplates;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class KafkaMaintenance extends Abstract {

    public static final String NAMESPACE = "test-strimzi-kafka";

    @Test
    void deployNodepoolKafka() {
        String kafkaClusterName = "eredin";
        String pool1Name = "dual";
        String pool2Name = "controller";
        String pool3Name = "broker";

        ConfigMap configMap = ConfigMapTemplates.kafkaConfigMap(NAMESPACE, kafkaClusterName).build();
        ClusterManager.getInstance().getClient(EClusters.WORKER_01).getClient().configMaps().inNamespace(NAMESPACE).resource(configMap).create();

        // Create nodepools
        KafkaNodePool kafkaNodePool1 = KafkaNodePoolTemplates.defaultKafkaNodePool(NAMESPACE, pool1Name, kafkaClusterName, 5)
                .editSpec()
                .addToRoles(ProcessRoles.BROKER)
                .addToRoles(ProcessRoles.CONTROLLER)
                .withNewPersistentClaimStorage()
                    .withDeleteClaim(true)
                    .withSize("7Gi")
                .endPersistentClaimStorage()
                .endSpec()
                .build();
        ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaNodePoolClient().inNamespace(NAMESPACE).resource(kafkaNodePool1).create();

        KafkaNodePool kafkaNodePool2 = KafkaNodePoolTemplates.defaultKafkaNodePool(NAMESPACE, pool2Name, kafkaClusterName, 3)
                .editSpec()
                .addToRoles(ProcessRoles.CONTROLLER)
                .withNewPersistentClaimStorage()
                    .withDeleteClaim(true)
                    .withSize("5Gi")
                .endPersistentClaimStorage()
                .endSpec()
                .build();
        ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaNodePoolClient().inNamespace(NAMESPACE).resource(kafkaNodePool2).create();

        KafkaNodePool kafkaNodePool3 = KafkaNodePoolTemplates.defaultKafkaNodePool(NAMESPACE, pool3Name, kafkaClusterName, 7)
                .editSpec()
                .addToRoles(ProcessRoles.BROKER)
                .withNewPersistentClaimStorage()
                .withDeleteClaim(true)
                .withSize("10Gi")
                .endPersistentClaimStorage()
                .endSpec()
                .build();
        ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaNodePoolClient().inNamespace(NAMESPACE).resource(kafkaNodePool3).create();
        // Create kafka
        Kafka kafka = KafkaTemplates.kafka(NAMESPACE, kafkaClusterName, 3, 3).build();
        ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaClient().inNamespace(NAMESPACE).resource(kafka).create();

        Utils.waitFor("Kafka readiness", 1000, 300000,
            () -> {
                KafkaStatus status = ClusterManager.getInstance().getClient(EClusters.WORKER_01).kafkaClient().inNamespace(NAMESPACE).withName(kafkaClusterName).get().getStatus();
                LOGGER.info("Kafka {} status is {}", kafkaClusterName, status);

                return status.getConditions().stream().anyMatch(condition -> condition.getType().equals("Ready") && condition.getStatus().equals("True"));
            });
    }

    @BeforeAll
    void setupEnvironment() {
        // Create namespace
        Namespace ns = new NamespaceBuilder().withNewMetadata().withName(NAMESPACE).endMetadata().build();
        LOGGER.info("Creating namespace: {}", ns);
        ClusterManager.getInstance().getClient(EClusters.WORKER_01).getClient().namespaces().resource(ns).create();
    }

    @AfterAll
    void teardownEnvironment() {
        // Delete namespace
        LOGGER.info("Deleting namespace: {}", NAMESPACE);
        ClusterManager.getInstance().getClient(EClusters.WORKER_01).getClient().namespaces().withName(NAMESPACE).delete();
    }
}


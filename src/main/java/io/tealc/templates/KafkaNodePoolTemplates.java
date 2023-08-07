/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.templates;

import io.strimzi.api.kafka.model.nodepool.KafkaNodePoolBuilder;
import io.strimzi.api.kafka.model.nodepool.ProcessRoles;
import io.tealc.Constants;

import java.util.Map;

public class KafkaNodePoolTemplates {

    private KafkaNodePoolTemplates() {}

    public static KafkaNodePoolBuilder defaultKafkaNodePool(String namespaceName, String nodePoolName, String kafkaClusterName, int kafkaReplicas) {
        return new KafkaNodePoolBuilder()
            .withNewMetadata()
                .withNamespace(namespaceName)
                .withName(nodePoolName)
                .withLabels(Map.of(Constants.STRIMZI_CLUSTER_LABEL, kafkaClusterName))
            .endMetadata()
            .withNewSpec()
                .withReplicas(kafkaReplicas)
            .endSpec();
    }

    public static KafkaNodePoolBuilder kafkaNodePoolWithBrokerRole(String namespaceName, String nodePoolName, String kafkaClusterName, int kafkaReplicas) {
        return defaultKafkaNodePool(namespaceName, nodePoolName, kafkaClusterName, kafkaReplicas)
            .editOrNewSpec()
                .addToRoles(ProcessRoles.BROKER)
            .endSpec();
    }
}

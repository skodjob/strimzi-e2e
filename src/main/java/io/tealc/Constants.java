/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc;

/**
 * Interface for keep global constants used across system tests.
 */
public interface Constants {

    /**
     * Basic paths to examples
     */
    String PATH_TO_EXAMPLES = Utils.USER_PATH + "/src/main/resources";
    String PATH_TO_PACKAGING_INSTALL_FILES = Utils.USER_PATH + "/../packaging/install";

    /**
     * File paths for metrics YAMLs
     */
    String PATH_TO_KAFKA_METRICS_CONFIG = PATH_TO_EXAMPLES + "/kafka/kafka.yaml";
    String PATH_TO_METRICS_CM = PATH_TO_EXAMPLES + "/kafka/metrics.yaml";

    String KAFKA_METRICS_CONFIG_MAP_SUFFIX = "-kafka-metrics";

    /**
     * Strimzi domain used for the Strimzi labels
     */
    String STRIMZI_DOMAIN = "strimzi.io/";

    /**
     * Kubernetes domain used for Kubernetes labels
     */
    String KUBERNETES_DOMAIN = "app.kubernetes.io/";

    /**
     * The kind of a Kubernetes / OpenShift Resource. It contains the same value as the Kind of the corresponding
     * Custom Resource. It should have on of the following values:
     *
     * <ul>
     *   <li>Kafka</li>
     *   <li>KafkaConnect</li>
     *   <li>KafkaMirrorMaker</li>
     *   <li>KafkaBridge</li>
     *   <li>KafkaUser</li>
     *   <li>KafkaTopic</li>
     * </ul>
     */
    String STRIMZI_KIND_LABEL = STRIMZI_DOMAIN + "kind";

    /**
     * The Strimzi cluster the resource is part of. This is typically the name of the custom resource.
     */
    String STRIMZI_CLUSTER_LABEL = STRIMZI_DOMAIN + "cluster";
    /**
     * Annotation for enabling or disabling the Node Pools. This annotation is used on the Kafka CR
     */
    String ANNO_STRIMZI_IO_NODE_POOLS = STRIMZI_DOMAIN + "node-pools";
}

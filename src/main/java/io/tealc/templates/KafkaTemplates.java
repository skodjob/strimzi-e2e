/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.templates;

import io.strimzi.api.kafka.model.Kafka;
import io.strimzi.api.kafka.model.KafkaBuilder;
import io.strimzi.api.kafka.model.listener.arraylistener.GenericKafkaListenerBuilder;
import io.strimzi.api.kafka.model.listener.arraylistener.KafkaListenerType;
import io.tealc.Constants;
import io.tealc.Environment;
import io.tealc.Utils;

public class KafkaTemplates {

    private KafkaTemplates() {}

    private static final String KAFKA_METRICS_CONFIG_REF_KEY = "kafka-metrics-config.yml";
    private static final String ZOOKEEPER_METRICS_CONFIG_REF_KEY = "zookeeper-metrics-config.yml";
    private static final String CRUISE_CONTROL_METRICS_CONFIG_REF_KEY = "cruise-control-metrics-config.yml";

    public static KafkaBuilder kafka(String namespace, String name, int kafkaReplicas, int zookeeperReplicas) {
        Kafka kafka = getKafkaFromYaml(Constants.PATH_TO_KAFKA_METRICS_CONFIG);
        String metricsConfigMapName = name + Constants.KAFKA_METRICS_CONFIG_MAP_SUFFIX;
        return defaultKafka(kafka, namespace, name, kafkaReplicas, zookeeperReplicas)
            .editOrNewMetadata()
                .withNamespace(namespace)
            .endMetadata()
            .editSpec()
                .withNewKafkaExporter()
                .endKafkaExporter()
                .editKafka()
                    .withNewJmxPrometheusExporterMetricsConfig()
                        .withNewValueFrom()
                            .withNewConfigMapKeyRef(KAFKA_METRICS_CONFIG_REF_KEY, metricsConfigMapName, false)
                        .endValueFrom()
                    .endJmxPrometheusExporterMetricsConfig()
                .endKafka()
                .editZookeeper()
                    .withNewJmxPrometheusExporterMetricsConfig()
                        .withNewValueFrom()
                            .withNewConfigMapKeyRef(ZOOKEEPER_METRICS_CONFIG_REF_KEY, metricsConfigMapName, false)
                        .endValueFrom()
                    .endJmxPrometheusExporterMetricsConfig()
                .endZookeeper()
                .editCruiseControl()
                    // Extend active users tasks as we
                    .addToConfig("max.active.user.tasks", 10)
                    .withNewJmxPrometheusExporterMetricsConfig()
                        .withNewValueFrom()
                            .withNewConfigMapKeyRef(CRUISE_CONTROL_METRICS_CONFIG_REF_KEY, metricsConfigMapName, false)
                        .endValueFrom()
                    .endJmxPrometheusExporterMetricsConfig()
                .endCruiseControl()
            .endSpec();
    }

    private static KafkaBuilder defaultKafka(Kafka kafka, String namespace, String name, int kafkaReplicas, int zookeeperReplicas) {
        KafkaBuilder kb = new KafkaBuilder(kafka)
            .withNewMetadata()
                .withName(name)
                .withNamespace(namespace)
                .addToAnnotations(Constants.ANNO_STRIMZI_IO_NODE_POOLS, "enabled")
                .addToLabels("mode", "kraft")
            .endMetadata()
            .editSpec()
                .editKafka()
                    .withVersion(Environment.KAFKA_VERSION)
                    .withReplicas(kafkaReplicas)
                    .addToConfig("log.message.format.version", Environment.KAFKA_VERSION)
                    .addToConfig("inter.broker.protocol.version", Environment.KAFKA_VERSION)
                    .addToConfig("offsets.topic.replication.factor", Math.min(kafkaReplicas, 3))
                    .addToConfig("transaction.state.log.min.isr", Math.min(kafkaReplicas, 2))
                    .addToConfig("transaction.state.log.replication.factor", Math.min(kafkaReplicas, 3))
                    .addToConfig("default.replication.factor", Math.min(kafkaReplicas, 3))
                    .addToConfig("min.insync.replicas", Math.min(Math.max(kafkaReplicas - 1, 1), 2))
                    .withListeners(new GenericKafkaListenerBuilder()
                                .withName("plain")
                                .withPort(9092)
                                .withType(KafkaListenerType.INTERNAL)
                                .withTls(false)
                                .build(),
                            new GenericKafkaListenerBuilder()
                                .withName("tls")
                                .withPort(9093)
                                .withType(KafkaListenerType.INTERNAL)
                                .withTls(true)
                                .build())
                    .withNewInlineLogging()
                        .addToLoggers("kafka.root.logger.level", "DEBUG")
                    .endInlineLogging()
                .endKafka()
                .editZookeeper()
                    .withReplicas(zookeeperReplicas)
                    .withNewInlineLogging()
                        .addToLoggers("zookeeper.root.logger", "DEBUG")
                    .endInlineLogging()
                .endZookeeper()
                .editEntityOperator()
                    .editUserOperator()
                        .withNewInlineLogging()
                            .addToLoggers("rootLogger.level", "DEBUG")
                        .endInlineLogging()
                    .endUserOperator()
                    .editTopicOperator()
                        .withNewInlineLogging()
                            .addToLoggers("rootLogger.level", "DEBUG")
                        .endInlineLogging()
                    .endTopicOperator()
                .endEntityOperator()
            .endSpec();

        return kb;
    }

    private static Kafka getKafkaFromYaml(String yamlPath) {
        return Utils.configFromYaml(yamlPath, Kafka.class);
    }
}

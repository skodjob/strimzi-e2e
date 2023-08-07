/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc.templates;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.tealc.Constants;
import io.tealc.Utils;

public class ConfigMapTemplates {
    private ConfigMapTemplates() {}

    public static ConfigMapBuilder kafkaConfigMap(String namespace, String clusterName) {
        ConfigMap configMap = getConfigMapFromYaml(Constants.PATH_TO_METRICS_CM);
        return new ConfigMapBuilder(configMap)
            .editMetadata()
                .withName(clusterName + Constants.KAFKA_METRICS_CONFIG_MAP_SUFFIX)
                .withNamespace(namespace)
            .endMetadata();
    }

    private static ConfigMap getConfigMapFromYaml(String yamlPath) {
        return Utils.configFromYaml(yamlPath, ConfigMap.class);
    }
}

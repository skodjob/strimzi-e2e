/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClusterManager {
    private static ClusterManager instance;

    public static final Map<EClusters, KubeClient> CLIENTS = new LinkedHashMap<>();

    public static synchronized ClusterManager getInstance() {
        if (instance == null) {
            instance = new ClusterManager();
        }

        Config config = new ConfigBuilder()
                .withOauthToken(Environment.WORKER_01_TOKEN)
                .withMasterUrl(Environment.WORKER_01_URL)
                .withDisableHostnameVerification(true)
                .withTrustCerts(true)
                .build();

        CLIENTS.put(EClusters.WORKER_01, new KubeClient(config, "default"));

        config = new ConfigBuilder()
                .withOauthToken(Environment.WORKER_02_TOKEN)
                .withMasterUrl(Environment.WORKER_02_URL)
                .withDisableHostnameVerification(true)
                .withTrustCerts(true)
                .build();

        CLIENTS.put(EClusters.WORKER_02, new KubeClient(config, "default"));

        config = new ConfigBuilder()
                .withOauthToken(Environment.WORKER_03_TOKEN)
                .withMasterUrl(Environment.WORKER_03_URL)
                .withDisableHostnameVerification(true)
                .withTrustCerts(true)
                .build();

        CLIENTS.put(EClusters.WORKER_03, new KubeClient(config, "default"));

        return instance;
    }

    public KubeClient getClient(EClusters clusterName) {
        return CLIENTS.get(clusterName);
    }

    public void addClient(EClusters clusterName, KubeClient client) {
        CLIENTS.put(clusterName, client);
    }
}

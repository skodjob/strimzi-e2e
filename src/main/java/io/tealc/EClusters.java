/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc;

public enum EClusters {
    WORKER_01("worker-01"),
    WORKER_02("worker-02"),
    WORKER_03("worker-03");

    private final String clusterName;

    public String getClusterName() {
        return clusterName;
    }

    private EClusters(String clusterName) {
        this.clusterName = clusterName;
    }
}

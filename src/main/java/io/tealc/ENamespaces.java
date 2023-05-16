/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc;

public enum ENamespaces {
    KAFKA("strimzi-kafka"),
    KAFKA_KRAFT("strimzi-kafka-kraft"),
    OPERATOR("strimzi-operator"),
    OPERATOR_KRAFT("strimzi-operator-kraft"),
    MIRROR_MAKER("strimzi-mirror"),
    MIRROR_MAKER_KRAFT("strimzi-mirror-kraft"),
    CONNECT("strimzi-connect"),
    CLIENTS("strimzi-clients"),
    CLIENTS_KRAFT("strimzi-clients-kraft"),
    DRAIN_CLEANER("strimzi-drain-cleaner");

    public final String name;

    private ENamespaces(String name) {
        this.name = name;
    }
}

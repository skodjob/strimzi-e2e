/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * Class which holds environment variables for system tests.
 */
public class Environment {

    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);
    private static final Map<String, String> VALUES = new HashMap<>();

    public static final String WORKER_01_TOKEN_ENV = "WORKER_01_OCP_TOKEN";
    public static final String WORKER_01_URL_ENV = "WORKER_01_URL";
    public static final String WORKER_02_TOKEN_ENV = "WORKER_02_OCP_TOKEN";
    public static final String WORKER_02_URL_ENV = "WORKER_02_URL";
    public static final String WORKER_03_TOKEN_ENV = "WORKER_03_OCP_TOKEN";
    public static final String WORKER_03_URL_ENV = "WORKER_03_URL";
    public static final String KAFKA_VERSION_ENV = "KAFKA_VERSION";

    /**
     * Set values
     */
    public static final String WORKER_01_TOKEN = getOrDefault(WORKER_01_TOKEN_ENV, null);
    public static final String WORKER_01_URL = getOrDefault(WORKER_01_URL_ENV, null);
    public static final String WORKER_02_TOKEN = getOrDefault(WORKER_02_TOKEN_ENV, null);
    public static final String WORKER_02_URL = getOrDefault(WORKER_02_URL_ENV, null);
    public static final String WORKER_03_TOKEN = getOrDefault(WORKER_03_TOKEN_ENV, null);
    public static final String WORKER_03_URL = getOrDefault(WORKER_03_URL_ENV, null);
    public static final String KAFKA_VERSION = getOrDefault(KAFKA_VERSION_ENV, "3.5.1");

    private Environment() { }

    static {
        String debugFormat = "{}: {}";
        LOGGER.info("Used environment variables:");
        VALUES.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (!entry.getKey().toLowerCase(Locale.ROOT).contains("pass")) {
                        LOGGER.info(debugFormat, entry.getKey(), entry.getValue());
                    } else {
                        LOGGER.info(debugFormat, entry.getKey(), "*****");
                    }
                });
    }

    private static String getOrDefault(String varName, String defaultValue) {
        return getOrDefault(varName, String::toString, defaultValue);
    }

    private static <T> T getOrDefault(String var, Function<String, T> converter, T defaultValue) {
        String value = System.getenv(var);
        T returnValue = defaultValue;
        if (value != null) {
            returnValue = converter.apply(value);
        }
        VALUES.put(var, String.valueOf(returnValue));
        return returnValue;
    }
}

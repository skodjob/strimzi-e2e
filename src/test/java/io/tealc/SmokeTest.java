/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

public class SmokeTest extends Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmokeTest.class);

    @Test
    void testKafkaIsDeployed() {
        LOGGER.info("Indeed");
        assertThat(getClient().namespace(KAFKA_NAMESPACE).listPods(), is(not(empty())));
    }

    @Test
    void testStrimziIsDeployed() {
        LOGGER.info("Indeed");
        assertThat(getClient().namespace(STRIMZI_NAMESPACE).listPods(), is(not(empty())));
    }
}

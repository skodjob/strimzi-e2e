# strimzi-e2e

[![Build & Push](https://github.com/skodjob/strimzi-e2e/actions/workflows/verify.yaml/badge.svg)](https://github.com/skodjob/strimzi-e2e/actions/workflows/verify.yaml)

Simple junit5 test suite for [Strimzi](https://github.com/strimzi/strimzi-kafka-operator) scenario [deployed](https://github.com/skodjob/sokar/tree/main/strimzi).

The core is re-used from Strimzi [systemtest](https://github.com/strimzi/strimzi-kafka-operator/tree/main/systemtest) test suite.
The difference is, that this test suite is focused on scenarios that are running for a long time.

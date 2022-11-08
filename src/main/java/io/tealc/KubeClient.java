/*
 * Copyright Tealc authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.tealc;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import io.fabric8.openshift.client.OpenShiftClient;
import io.strimzi.api.kafka.Crds;
import io.strimzi.api.kafka.KafkaConnectList;
import io.strimzi.api.kafka.KafkaConnectorList;
import io.strimzi.api.kafka.KafkaList;
import io.strimzi.api.kafka.KafkaTopicList;
import io.strimzi.api.kafka.KafkaUserList;
import io.strimzi.api.kafka.model.Kafka;
import io.strimzi.api.kafka.model.KafkaConnect;
import io.strimzi.api.kafka.model.KafkaConnector;
import io.strimzi.api.kafka.model.KafkaTopic;
import io.strimzi.api.kafka.model.KafkaUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class KubeClient {
    protected final KubernetesClient client;
    protected String namespace;

    private static final Logger LOGGER = LoggerFactory.getLogger(KubeClient.class);

    public KubeClient(String namespace) {
        LOGGER.debug("Creating client in namespace: {}", namespace);
        Config config = Config.autoConfigure(System.getenv().getOrDefault("KUBE_CONTEXT", null));

        this.client = new KubernetesClientBuilder()
                .withConfig(config)
                .build()
                .adapt(OpenShiftClient.class);
        this.namespace = namespace;
    }

    public KubeClient(KubernetesClient client, String namespace) {
        LOGGER.debug("Creating client in namespace: {}", namespace);
        this.client = client;
        this.namespace = namespace;
    }

    // ============================
    // ---------> CLIENT <---------
    // ============================

    public KubernetesClient getClient() {
        return client;
    }

    // ===============================
    // ---------> NAMESPACE <---------
    // ===============================

    public KubeClient inNamespace(String namespace) {
        LOGGER.debug("Using namespace: {}", namespace);
        this.namespace = namespace;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public Namespace getNamespace(String namespace) {
        return client.namespaces().withName(namespace).get();
    }

    public boolean namespaceExists(String namespace) {
        return client.namespaces().list().getItems().stream().map(n -> n.getMetadata().getName())
            .collect(Collectors.toList()).contains(namespace);
    }

    /**
     * Gets namespace status
     */
    public boolean getNamespaceStatus(String namespaceName) {
        return client.namespaces().withName(namespaceName).isReady();
    }

    // ================================
    // ---------> CONFIG MAP <---------
    // ================================
    public ConfigMap getConfigMap(String namespaceName, String configMapName) {
        return client.configMaps().inNamespace(namespaceName).withName(configMapName).get();
    }

    public ConfigMap getConfigMap(String configMapName) {
        return getConfigMap(namespace, configMapName);
    }


    public boolean getConfigMapStatus(String configMapName) {
        return client.configMaps().inNamespace(getNamespace()).withName(configMapName).isReady();
    }

    // =========================
    // ---------> POD <---------
    // =========================
    public List<Pod> listPods() {
        return client.pods().inNamespace(namespace).list().getItems();
    }

    public List<Pod> listPods(String namespaceName) {
        return client.pods().inNamespace(namespaceName).list().getItems();
    }

    /**
     * Returns list of pods by prefix in pod name
     * @param namespaceName Namespace name
     * @param podNamePrefix prefix with which the name should begin
     * @return List of pods
     */
    public List<Pod> listPodsByPrefixInName(String namespaceName, String podNamePrefix) {
        return listPods(namespaceName)
                .stream().filter(p -> p.getMetadata().getName().startsWith(podNamePrefix))
                .collect(Collectors.toList());
    }

    /**
     * Gets pod
     */
    public Pod getPod(String namespaceName, String name) {
        return client.pods().inNamespace(namespaceName).withName(name).get();
    }

    public Pod getPod(String name) {
        return getPod(namespace, name);
    }

    // ==================================
    // ---------> STATEFUL SET <---------
    // ==================================

    /**
     * Gets stateful set
     */
    public StatefulSet getStatefulSet(String namespaceName, String statefulSetName) {
        return client.apps().statefulSets().inNamespace(namespaceName).withName(statefulSetName).get();
    }

    public StatefulSet getStatefulSet(String statefulSetName) {
        return getStatefulSet(namespace, statefulSetName);
    }

    /**
     * Gets stateful set
     */
    public RollableScalableResource<StatefulSet> statefulSet(String namespaceName, String statefulSetName) {
        return client.apps().statefulSets().inNamespace(namespaceName).withName(statefulSetName);
    }

    public RollableScalableResource<StatefulSet> statefulSet(String statefulSetName) {
        return statefulSet(namespace, statefulSetName);
    }
    // ================================
    // ---------> DEPLOYMENT <---------
    // ================================

    /**
     * Gets deployment
     */

    public Deployment getDeployment(String namespaceName, String deploymentName) {
        return client.apps().deployments().inNamespace(namespaceName).withName(deploymentName).get();
    }

    public Deployment getDeployment(String deploymentName) {
        return client.apps().deployments().inNamespace(namespace).withName(deploymentName).get();
    }

    public Deployment getDeploymentFromAnyNamespaces(String deploymentName) {
        return client.apps().deployments().inAnyNamespace().list().getItems().stream().filter(
            deployment -> deployment.getMetadata().getName().equals(deploymentName))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Gets deployment status
     */
    public LabelSelector getDeploymentSelectors(String namespaceName, String deploymentName) {
        return client.apps().deployments().inNamespace(namespaceName).withName(deploymentName).get().getSpec().getSelector();
    }

    // ==========================
    // ---------> NODE <---------
    // ==========================

    public String getNodeAddress() {
        return listNodes().get(0).getStatus().getAddresses().get(0).getAddress();
    }

    public List<Node> listNodes() {
        return client.nodes().list().getItems();
    }

    public List<Node> listWorkerNodes() {
        return listNodes().stream().filter(node -> node.getMetadata().getLabels().containsKey("node-role.kubernetes.io/worker")).collect(Collectors.toList());
    }

    public List<Node> listMasterNodes() {
        return listNodes().stream().filter(node -> node.getMetadata().getLabels().containsKey("node-role.kubernetes.io/master")).collect(Collectors.toList());
    }

    // =========================
    // ---------> JOB <---------
    // =========================

    public boolean jobExists(String jobName) {
        return client.batch().v1().jobs().inNamespace(namespace).list().getItems().stream().anyMatch(j -> j.getMetadata().getName().startsWith(jobName));
    }

    public Job getJob(String jobName) {
        return client.batch().v1().jobs().inNamespace(namespace).withName(jobName).get();
    }

    public boolean checkSucceededJobStatus(String jobName) {
        return checkSucceededJobStatus(getNamespace(), jobName, 1);
    }

    public boolean checkSucceededJobStatus(String namespaceName, String jobName, int expectedSucceededPods) {
        return getJobStatus(namespaceName, jobName).getSucceeded().equals(expectedSucceededPods);
    }

    public boolean checkFailedJobStatus(String namespaceName, String jobName, int expectedFailedPods) {
        return getJobStatus(namespaceName, jobName).getFailed().equals(expectedFailedPods);
    }

    // Pods Statuses:  0 Running / 0 Succeeded / 1 Failed
    public JobStatus getJobStatus(String namespaceName, String jobName) {
        return client.batch().v1().jobs().inNamespace(namespaceName).withName(jobName).get().getStatus();
    }

    public JobStatus getJobStatus(String jobName) {
        return getJobStatus(namespace, jobName);
    }

    public JobList getJobList() {
        return client.batch().v1().jobs().inNamespace(namespace).list();
    }

    public List<Job> listJobs(String namePrefix) {
        return client.batch().v1().jobs().inNamespace(getNamespace()).list().getItems().stream()
            .filter(job -> job.getMetadata().getName().startsWith(namePrefix)).collect(Collectors.toList());
    }

    public MixedOperation<Kafka, KafkaList, Resource<Kafka>> kafkaClient() {
        return Crds.kafkaOperation(client);
    }

    public MixedOperation<KafkaConnect, KafkaConnectList, Resource<KafkaConnect>> kafkaConnectClient() {
        return Crds.kafkaConnectOperation(client);
    }

    public MixedOperation<KafkaConnector, KafkaConnectorList, Resource<KafkaConnector>> kafkaConnectorClient() {
        return Crds.kafkaConnectorOperation(client);
    }

    public MixedOperation<KafkaUser, KafkaUserList, Resource<KafkaUser>> kafkaUserClient() {
        return Crds.kafkaUserOperation(client);
    }

    public MixedOperation<KafkaTopic, KafkaTopicList, Resource<KafkaTopic>> kafkaTopicClient() {
        return Crds.topicOperation(client);
    }
}

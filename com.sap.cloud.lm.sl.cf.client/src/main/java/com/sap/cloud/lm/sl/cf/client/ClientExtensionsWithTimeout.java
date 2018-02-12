package com.sap.cloud.lm.sl.cf.client;

import static com.sap.cloud.lm.sl.cf.client.util.FunctionUtil.callable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.domain.ServiceKey;

import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudServiceOfferingExtended;
import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudTask;
import com.sap.cloud.lm.sl.cf.client.lib.domain.UploadInfo;
import com.sap.cloud.lm.sl.cf.client.lib.domain.UploadStatusCallbackExtended;
import com.sap.cloud.lm.sl.cf.client.util.TimeoutExecutor;

public class ClientExtensionsWithTimeout implements ClientExtensions {

    private static final int START_TIMEOUT = 240;
    private static final int STAGE_TIMEOUT = 270;

    private ClientExtensions client;

    private TimeoutExecutor timeoutExecutor;

    public ClientExtensionsWithTimeout(ClientExtensions client) {
        this(client, TimeoutExecutor.getInstance());
    }

    public ClientExtensionsWithTimeout(ClientExtensions client, TimeoutExecutor timeoutExecutor) {
        this.client = client;
        this.timeoutExecutor = timeoutExecutor;
    }

    @Override
    public void registerServiceURL(String serviceName, String serviceUrl) {
        executeWithTimeout(callable(() -> client.registerServiceURL(serviceName, serviceUrl)));
    }

    @Override
    public void unregisterServiceURL(String serviceName) {
        executeWithTimeout(callable(() -> client.unregisterServiceURL(serviceName)));
    }

    @Override
    public void updateServiceParameters(String serviceName, Map<String, Object> parameters) {
        executeWithTimeout(callable(() -> client.updateServiceParameters(serviceName, parameters)));
    }

    @Override
    public void bindService(String appName, String serviceName, Map<String, Object> parameters) {
        executeWithTimeout(callable(() -> client.bindService(appName, serviceName, parameters)));
    }

    @Override
    public void updateUserProvidedServiceCredentials(String serviceName, Map<String, Object> credentials) {
        executeWithTimeout(callable(() -> client.updateUserProvidedServiceCredentials(serviceName, credentials)));
    }

    @Override
    public StartingInfo startApplication(String appName, boolean staging) {
        return timeoutExecutor.executeWithTimeout(() -> client.startApplication(appName, staging), false, START_TIMEOUT);
    }

    @Override
    public StartingInfo stageApplication(String appName) {
        return timeoutExecutor.executeWithTimeout(() -> client.stageApplication(appName), STAGE_TIMEOUT);
    }

    @Override
    public void updateServiceTags(String serviceName, List<String> serviceTags) {
        executeWithTimeout(callable(() -> client.updateServiceTags(serviceName, serviceTags)));
    }

    @Override
    public int reserveTcpPort(String domain, boolean tcps) {
        return executeWithTimeout(() -> client.reserveTcpPort(domain, tcps));
    }

    @Override
    public int reserveTcpPort(int port, String domain, boolean tcps) {
        return executeWithTimeout(() -> client.reserveTcpPort(port, domain, tcps));
    }

    @Override
    public int reservePort(String domain) {
        return executeWithTimeout(() -> client.reservePort(domain));
    }

    @Override
    public String asynchUploadApplication(String appName, File file, UploadStatusCallbackExtended callback) throws IOException {
        return executeWithTimeout(() -> client.asynchUploadApplication(appName, file, callback));
    }

    @Override
    public UploadInfo getUploadProgress(String uploadToken) {
        return executeWithTimeout(() -> client.getUploadProgress(uploadToken));
    }

    @Override
    public ServiceKey createServiceKey(String serviceName, String serviceKey, String parameters) {
        return executeWithTimeout(() -> client.createServiceKey(serviceName, serviceKey, parameters));
    }

    @Override
    public void deleteServiceKey(String serviceName, String serviceKey) {
        executeWithTimeout(callable(() -> client.deleteServiceKey(serviceName, serviceKey)));
    }

    @Override
    public List<CloudServiceOfferingExtended> getExtendedServiceOfferings() {
        return executeWithTimeout(() -> client.getExtendedServiceOfferings());
    }

    @Override
    public void addRoute(String host, String domainName, String path) {
        executeWithTimeout(callable(() -> client.addRoute(host, domainName, path)));
    }

    @Override
    public void deleteRoute(String host, String domainName, String path) {
        executeWithTimeout(callable(() -> client.deleteRoute(host, domainName, path)));
    }

    @Override
    public List<CloudTask> getTasks(String appName) throws UnsupportedOperationException {
        return executeWithTimeout(() -> client.getTasks(appName));
    }

    @Override
    public CloudTask runTask(String appName, String taskName, String command) throws UnsupportedOperationException {
        return executeWithTimeout(() -> client.runTask(appName, taskName, command));
    }

    @Override
    public CloudTask runTask(String appName, String taskName, String command, Map<String, String> environment)
        throws UnsupportedOperationException {
        return executeWithTimeout(() -> client.runTask(appName, taskName, command, environment));
    }

    @Override
    public CloudTask cancelTask(UUID taskId) throws UnsupportedOperationException {
        return executeWithTimeout(() -> client.cancelTask(taskId));
    }

    @Override
    public void updateServicePlan(String serviceName, String planName) {
        executeWithTimeout(callable(() -> client.updateServicePlan(serviceName, planName)));
    }

    private <T> T executeWithTimeout(Callable<T> task) {
        return timeoutExecutor.executeWithTimeout(task, true);
    }

}

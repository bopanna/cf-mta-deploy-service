package com.sap.cloud.lm.sl.cf.client;

import static com.sap.cloud.lm.sl.cf.client.util.FunctionUtil.callable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.cloudfoundry.client.lib.ApplicationLogListener;
import org.cloudfoundry.client.lib.ClientHttpResponseCallback;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryOperations;
import org.cloudfoundry.client.lib.RestLogCallback;
import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.StreamingLogToken;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.domain.ApplicationLog;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudApplication.DebugMode;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudEvent;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudQuota;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudSecurityGroup;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceBroker;
import org.cloudfoundry.client.lib.domain.CloudServiceInstance;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CloudStack;
import org.cloudfoundry.client.lib.domain.CloudUser;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.ServiceKey;
import org.cloudfoundry.client.lib.domain.Staging;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.ResponseErrorHandler;

import com.sap.cloud.lm.sl.cf.client.util.TimeoutExecutor;

public class CloudfoundryOperationsWithTimeout implements CloudFoundryOperations {

    private static final int START_TIMEOUT = 240;
    private static final int UPLOAD_TIMEOUT = 240;

    private CloudFoundryOperations client;
    private TimeoutExecutor timeoutExecutor;

    public CloudfoundryOperationsWithTimeout(CloudFoundryOperations client) {
        this(client, TimeoutExecutor.getInstance());
    }

    public CloudfoundryOperationsWithTimeout(CloudFoundryOperations client, TimeoutExecutor timeoutExecutor) {
        this.client = client;
        this.timeoutExecutor = timeoutExecutor;
    }

    @Override
    public void addDomain(String domainName) {
        executeWithTimeout(callable(() -> client.addDomain(domainName)));
    }

    @Override
    public void addRoute(String host, String domainName) {
        executeWithTimeout(callable(() -> client.addRoute(host, domainName)));
    }

    @Override
    public void associateAuditorWithSpace(String spaceName) {
        executeWithTimeout(callable(() -> client.associateAuditorWithSpace(spaceName)));
    }

    @Override
    public void associateAuditorWithSpace(String orgName, String spaceName) {
        executeWithTimeout(callable(() -> client.associateAuditorWithSpace(orgName, spaceName)));
    }

    @Override
    public void associateAuditorWithSpace(String orgName, String spaceName, String userGuid) {
        executeWithTimeout(callable(() -> client.associateAuditorWithSpace(orgName, spaceName, userGuid)));
    }

    @Override
    public void associateDeveloperWithSpace(String spaceName) {
        executeWithTimeout(callable(() -> client.associateDeveloperWithSpace(spaceName)));
    }

    @Override
    public void associateDeveloperWithSpace(String orgName, String spaceName) {
        executeWithTimeout(callable(() -> client.associateDeveloperWithSpace(orgName, spaceName)));
    }

    @Override
    public void associateDeveloperWithSpace(String orgName, String spaceName, String userGuid) {
        executeWithTimeout(callable(() -> client.associateDeveloperWithSpace(orgName, spaceName, userGuid)));
    }

    @Override
    public void associateManagerWithSpace(String spaceName) {
        executeWithTimeout(callable(() -> client.associateManagerWithSpace(spaceName)));
    }

    @Override
    public void associateManagerWithSpace(String orgName, String spaceName) {
        executeWithTimeout(callable(() -> client.associateManagerWithSpace(orgName, spaceName)));
    }

    @Override
    public void associateManagerWithSpace(String orgName, String spaceName, String userGuid) {
        executeWithTimeout(callable(() -> client.associateManagerWithSpace(orgName, spaceName, userGuid)));
    }

    @Override
    public void bindRunningSecurityGroup(String securityGroupName) {
        executeWithTimeout(callable(() -> client.bindRunningSecurityGroup(securityGroupName)));
    }

    @Override
    public void bindSecurityGroup(String orgName, String spaceName, String securityGroupName) {
        executeWithTimeout(callable(() -> client.bindSecurityGroup(orgName, spaceName, securityGroupName)));
    }

    @Override
    public void bindService(String appName, String serviceName) {
        executeWithTimeout(callable(() -> client.bindService(appName, serviceName)));
    }

    @Override
    public void bindStagingSecurityGroup(String securityGroupName) {
        executeWithTimeout(callable(() -> client.bindStagingSecurityGroup(securityGroupName)));
    }

    @Override
    public void createApplication(String appName, Staging staging, Integer memory, List<String> uris, List<String> serviceNames) {
        executeWithTimeout(callable(() -> client.createApplication(appName, staging, memory, uris, serviceNames)));
    }

    @Override
    public void createApplication(String appName, Staging staging, Integer disk, Integer memory, List<String> uris,
        List<String> serviceNames) {
        executeWithTimeout(callable(() -> client.createApplication(appName, staging, disk, memory, uris, serviceNames)));
    }

    @Override
    public void createQuota(CloudQuota quota) {
        executeWithTimeout(callable(() -> client.createQuota(quota)));
    }

    @Override
    public void createSecurityGroup(CloudSecurityGroup securityGroup) {
        executeWithTimeout(callable(() -> client.createSecurityGroup(securityGroup)));
    }

    @Override
    public void createSecurityGroup(String name, InputStream jsonRulesFile) {
        executeWithTimeout(callable(() -> client.createSecurityGroup(name, jsonRulesFile)));
    }

    @Override
    public void createService(CloudService service) {
        executeWithTimeout(callable(() -> client.createService(service)));
    }

    @Override
    public void createServiceBroker(CloudServiceBroker serviceBroker) {
        executeWithTimeout(callable(() -> client.createServiceBroker(serviceBroker)));
    }

    @Override
    public void createSpace(String spaceName) {
        executeWithTimeout(callable(() -> client.createSpace(spaceName)));
    }

    @Override
    public void createUserProvidedService(CloudService service, Map<String, Object> credentials) {
        executeWithTimeout(callable(() -> client.createUserProvidedService(service, credentials)));
    }

    @Override
    public void createUserProvidedService(CloudService service, Map<String, Object> credentials, String syslogDrainUrl) {
        executeWithTimeout(callable(() -> client.createUserProvidedService(service, credentials, syslogDrainUrl)));
    }

    @Override
    public void debugApplication(String appName, DebugMode mode) {
        executeWithTimeout(callable(() -> client.debugApplication(appName, mode)));
    }

    @Override
    public void deleteAllApplications() {
        executeWithTimeout(callable(() -> client.deleteAllApplications()));
    }

    @Override
    public void deleteAllServices() {
        executeWithTimeout(callable(() -> client.deleteAllServices()));
    }

    @Override
    public void deleteApplication(String appName) {
        executeWithTimeout(callable(() -> client.deleteApplication(appName)));
    }

    @Override
    public void deleteDomain(String domainName) {
        executeWithTimeout(callable(() -> client.deleteDomain(domainName)));
    }

    @Override
    public List<CloudRoute> deleteOrphanedRoutes() {
        return executeWithTimeout(() -> client.deleteOrphanedRoutes());
    }

    @Override
    public void deleteQuota(String quotaName) {
        executeWithTimeout(callable(() -> client.deleteQuota(quotaName)));
    }

    @Override
    public void deleteRoute(String host, String domainName) {
        executeWithTimeout(callable(() -> client.deleteRoute(host, domainName)));
    }

    @Override
    public void deleteSecurityGroup(String securityGroupName) {
        executeWithTimeout(callable(() -> client.deleteSecurityGroup(securityGroupName)));
    }

    @Override
    public void deleteService(String service) {
        executeWithTimeout(callable(() -> client.deleteService(service)));
    }

    @Override
    public void deleteServiceBroker(String name) {
        executeWithTimeout(callable(() -> client.deleteServiceBroker(name)));
    }

    @Override
    public void deleteSpace(String spaceName) {
        executeWithTimeout(callable(() -> client.deleteSpace(spaceName)));
    }

    @Override
    public CloudApplication getApplication(String appName) {
        return executeWithTimeout(() -> client.getApplication(appName));
    }

    @Override
    public CloudApplication getApplication(String appName, boolean required) {
        return executeWithTimeout(() -> client.getApplication(appName, required));
    }

    @Override
    public CloudApplication getApplication(UUID guid) {

        return executeWithTimeout(() -> client.getApplication(guid));
    }

    @Override
    public CloudApplication getApplication(UUID guid, boolean required) {

        return executeWithTimeout(() -> client.getApplication(guid, required));
    }

    @Override
    public Map<String, Object> getApplicationEnvironment(String appName) {

        return executeWithTimeout(() -> client.getApplicationEnvironment(appName));
    }

    @Override
    public Map<String, Object> getApplicationEnvironment(UUID appGuid) {

        return executeWithTimeout(() -> client.getApplicationEnvironment(appGuid));
    }

    @Override
    public List<CloudEvent> getApplicationEvents(String appName) {

        return executeWithTimeout(() -> client.getApplicationEvents(appName));
    }

    @Override
    public InstancesInfo getApplicationInstances(String appName) {

        return executeWithTimeout(() -> client.getApplicationInstances(appName));
    }

    @Override
    public InstancesInfo getApplicationInstances(CloudApplication app) {
        return executeWithTimeout(() -> client.getApplicationInstances(app));
    }

    @Override
    public ApplicationStats getApplicationStats(String appName) {
        return executeWithTimeout(() -> client.getApplicationStats(appName));
    }

    @Override
    public List<CloudApplication> getApplications() {
        return executeWithTimeout(() -> client.getApplications());
    }

    @Override
    public URL getCloudControllerUrl() {
        return executeWithTimeout(() -> client.getCloudControllerUrl());
    }

    @Override
    public CloudInfo getCloudInfo() {
        return executeWithTimeout(() -> client.getCloudInfo());
    }

    @Override
    public Map<String, String> getCrashLogs(String appName) {
        return executeWithTimeout(() -> client.getCrashLogs(appName));
    }

    @Override
    public CrashesInfo getCrashes(String appName) {
        return executeWithTimeout(() -> client.getCrashes(appName));
    }

    @Override
    public CloudDomain getDefaultDomain() {
        return executeWithTimeout(() -> client.getDefaultDomain());
    }

    @Override
    public List<CloudDomain> getDomains() {
        return executeWithTimeout(() -> client.getDomains());
    }

    @Override
    public List<CloudDomain> getDomainsForOrg() {

        return executeWithTimeout(() -> client.getDomainsForOrg());
    }

    @Override
    public List<CloudEvent> getEvents() {

        return executeWithTimeout(() -> client.getEvents());
    }

    @Override
    public String getFile(String appName, int instanceIndex, String filePath) {

        return executeWithTimeout(() -> client.getFile(appName, instanceIndex, filePath));
    }

    @Override
    public String getFile(String appName, int instanceIndex, String filePath, int startPosition) {

        return executeWithTimeout(() -> client.getFile(appName, instanceIndex, filePath, startPosition));
    }

    @Override
    public String getFile(String appName, int instanceIndex, String filePath, int startPosition, int endPosition) {

        return executeWithTimeout(() -> client.getFile(appName, instanceIndex, filePath, startPosition, endPosition));
    }

    @Override
    public String getFileTail(String appName, int instanceIndex, String filePath, int length) {

        return executeWithTimeout(() -> client.getFileTail(appName, instanceIndex, filePath, length));
    }

    @Override
    public Map<String, String> getLogs(String appName) {

        return executeWithTimeout(() -> client.getLogs(appName));
    }

    @Override
    public CloudOrganization getOrganization(String orgName) {

        return executeWithTimeout(() -> client.getOrganization(orgName));
    }

    @Override
    public CloudOrganization getOrganization(String orgName, boolean required) {

        return executeWithTimeout(() -> client.getOrganization(orgName, required));
    }

    @Override
    public Map<String, CloudUser> getOrganizationUsers(String orgName) {

        return executeWithTimeout(() -> client.getOrganizationUsers(orgName));
    }

    @Override
    public List<CloudOrganization> getOrganizations() {

        return executeWithTimeout(() -> client.getOrganizations());
    }

    @Override
    public List<CloudDomain> getPrivateDomains() {

        return executeWithTimeout(() -> client.getPrivateDomains());
    }

    @Override
    public CloudQuota getQuota(String quotaName) {

        return executeWithTimeout(() -> client.getQuota(quotaName));
    }

    @Override
    public CloudQuota getQuota(String quotaName, boolean required) {

        return executeWithTimeout(() -> client.getQuota(quotaName, required));
    }

    @Override
    public List<CloudQuota> getQuotas() {

        return executeWithTimeout(() -> client.getQuotas());
    }

    @Override
    public List<ApplicationLog> getRecentLogs(String appName) {

        return executeWithTimeout(() -> client.getRecentLogs(appName));
    }

    @Override
    public List<CloudRoute> getRoutes(String domainName) {

        return executeWithTimeout(() -> client.getRoutes(domainName));
    }

    @Override
    public List<CloudSecurityGroup> getRunningSecurityGroups() {

        return executeWithTimeout(() -> client.getRunningSecurityGroups());
    }

    @Override
    public CloudSecurityGroup getSecurityGroup(String securityGroupName) {

        return executeWithTimeout(() -> client.getSecurityGroup(securityGroupName));
    }

    @Override
    public CloudSecurityGroup getSecurityGroup(String securityGroupName, boolean required) {

        return executeWithTimeout(() -> client.getSecurityGroup(securityGroupName, required));
    }

    @Override
    public List<CloudSecurityGroup> getSecurityGroups() {

        return executeWithTimeout(() -> client.getSecurityGroups());
    }

    @Override
    public CloudService getService(String service) {

        return executeWithTimeout(() -> client.getService(service));
    }

    @Override
    public CloudService getService(String service, boolean required) {

        return executeWithTimeout(() -> client.getService(service, required));
    }

    @Override
    public CloudServiceBroker getServiceBroker(String name) {

        return executeWithTimeout(() -> client.getServiceBroker(name));
    }

    @Override
    public CloudServiceBroker getServiceBroker(String name, boolean required) {

        return executeWithTimeout(() -> client.getServiceBroker(name));
    }

    @Override
    public List<CloudServiceBroker> getServiceBrokers() {

        return executeWithTimeout(() -> client.getServiceBrokers());
    }

    @Override
    public CloudServiceInstance getServiceInstance(String service) {

        return executeWithTimeout(() -> client.getServiceInstance(service));
    }

    @Override
    public CloudServiceInstance getServiceInstance(String service, boolean required) {

        return executeWithTimeout(() -> client.getServiceInstance(service, required));
    }

    @Override
    public List<ServiceKey> getServiceKeys(String serviceName) {

        return executeWithTimeout(() -> client.getServiceKeys(serviceName));
    }

    @Override
    public List<CloudServiceOffering> getServiceOfferings() {

        return executeWithTimeout(() -> client.getServiceOfferings());
    }

    @Override
    public List<CloudService> getServices() {

        return executeWithTimeout(() -> client.getServices());
    }

    @Override
    public List<CloudDomain> getSharedDomains() {

        return executeWithTimeout(() -> client.getSharedDomains());
    }

    @Override
    public CloudSpace getSpace(String spaceName) {

        return executeWithTimeout(() -> client.getSpace(spaceName));
    }

    @Override
    public CloudSpace getSpace(String spaceName, boolean required) {

        return executeWithTimeout(() -> client.getSpace(spaceName, required));
    }

    @Override
    public List<UUID> getSpaceAuditors(String spaceName) {

        return executeWithTimeout(() -> client.getSpaceAuditors(spaceName));
    }

    @Override
    public List<UUID> getSpaceAuditors(String orgName, String spaceName) {

        return executeWithTimeout(() -> client.getSpaceAuditors(orgName, spaceName));
    }

    @Override
    public List<UUID> getSpaceDevelopers(String spaceName) {

        return executeWithTimeout(() -> client.getSpaceDevelopers(spaceName));
    }

    @Override
    public List<UUID> getSpaceDevelopers(String orgName, String spaceName) {

        return executeWithTimeout(() -> client.getSpaceDevelopers(spaceName));
    }

    @Override
    public List<UUID> getSpaceManagers(String spaceName) {

        return executeWithTimeout(() -> client.getSpaceManagers(spaceName));
    }

    @Override
    public List<UUID> getSpaceManagers(String orgName, String spaceName) {

        return executeWithTimeout(() -> client.getSpaceManagers(orgName, spaceName));
    }

    @Override
    public List<CloudSpace> getSpaces() {

        return executeWithTimeout(() -> client.getSpaces());
    }

    @Override
    public List<CloudSpace> getSpacesBoundToSecurityGroup(String securityGroupName) {

        return executeWithTimeout(() -> client.getSpacesBoundToSecurityGroup(securityGroupName));
    }

    @Override
    public CloudStack getStack(String name) {

        return executeWithTimeout(() -> client.getStack(name));
    }

    @Override
    public CloudStack getStack(String name, boolean required) {

        return executeWithTimeout(() -> client.getStack(name, required));
    }

    @Override
    public List<CloudStack> getStacks() {

        return executeWithTimeout(() -> client.getStacks());
    }

    @Override
    public String getStagingLogs(StartingInfo info, int offset) {

        return executeWithTimeout(() -> client.getStagingLogs(info, offset));
    }

    @Override
    public List<CloudSecurityGroup> getStagingSecurityGroups() {

        return executeWithTimeout(() -> client.getStagingSecurityGroups());
    }

    @Override
    public OAuth2AccessToken login() {

        return executeWithTimeout(() -> client.login());
    }

    @Override
    public void logout() {
        executeWithTimeout(callable(() -> client.logout()));
    }

    @Override
    public void openFile(String appName, int instanceIndex, String filePath, ClientHttpResponseCallback clientHttpResponseCallback) {
        executeWithTimeout(callable(() -> client.openFile(appName, instanceIndex, filePath, clientHttpResponseCallback)));
    }

    @Override
    public void register(String email, String password) {
        executeWithTimeout(callable(() -> client.register(email, password)));
    }

    @Override
    public void registerRestLogListener(RestLogCallback callBack) {
        executeWithTimeout(callable(() -> client.registerRestLogListener(callBack)));
    }

    @Override
    public void removeDomain(String domainName) {
        executeWithTimeout(callable(() -> client.removeDomain(domainName)));
    }

    @Override
    public void rename(String appName, String newName) {
        executeWithTimeout(callable(() -> client.rename(appName, newName)));
    }

    @Override
    public StartingInfo restartApplication(String appName) {
        return executeWithTimeout(() -> client.restartApplication(appName));
    }

    @Override
    public void setQuotaToOrg(String orgName, String quotaName) {
        executeWithTimeout(callable(() -> client.setQuotaToOrg(orgName, quotaName)));
    }

    @Override
    public void setResponseErrorHandler(ResponseErrorHandler errorHandler) {
        executeWithTimeout(callable(() -> client.setResponseErrorHandler(errorHandler)));
    }

    @Override
    public StartingInfo startApplication(String appName) {
        return timeoutExecutor.executeWithTimeout(() -> client.startApplication(appName), false, START_TIMEOUT);
    }

    @Override
    public void stopApplication(String appName) {
        executeWithTimeout(callable(() -> client.stopApplication(appName)));
    }

    @Override
    public StreamingLogToken streamLogs(String appName, ApplicationLogListener listener) {
        return executeWithTimeout(() -> client.streamLogs(appName, listener));
    }

    @Override
    public void unRegisterRestLogListener(RestLogCallback callBack) {
        executeWithTimeout(callable(() -> client.unRegisterRestLogListener(callBack)));
    }

    @Override
    public void unbindRunningSecurityGroup(String securityGroupName) {
        executeWithTimeout(callable(() -> client.unbindRunningSecurityGroup(securityGroupName)));
    }

    @Override
    public void unbindSecurityGroup(String orgName, String spaceName, String securityGroupName) {
        executeWithTimeout(callable(() -> client.unbindSecurityGroup(orgName, spaceName, securityGroupName)));
    }

    @Override
    public void unbindService(String appName, String serviceName) {
        executeWithTimeout(callable(() -> client.unbindService(appName, serviceName)));
    }

    @Override
    public void unbindStagingSecurityGroup(String securityGroupName) {
        executeWithTimeout(callable(() -> client.unbindStagingSecurityGroup(securityGroupName)));
    }

    @Override
    public void unregister() {
        executeWithTimeout(callable(() -> client.unregister()));
    }

    @Override
    public void updateApplicationDiskQuota(String appName, int disk) {
        executeWithTimeout(callable(() -> client.updateApplicationDiskQuota(appName, disk)));
    }

    @Override
    public void updateApplicationEnv(String appName, Map<String, String> env) {
        executeWithTimeout(callable(() -> client.updateApplicationEnv(appName, env)));
    }

    @Override
    public void updateApplicationEnv(String appName, List<String> env) {
        executeWithTimeout(callable(() -> client.updateApplicationEnv(appName, env)));
    }

    @Override
    public void updateApplicationInstances(String appName, int instances) {
        executeWithTimeout(callable(() -> client.updateApplicationInstances(appName, instances)));
    }

    @Override
    public void updateApplicationMemory(String appName, int memory) {
        executeWithTimeout(callable(() -> client.updateApplicationMemory(appName, memory)));
    }

    @Override
    public void updateApplicationServices(String appName, List<String> services) {
        executeWithTimeout(callable(() -> client.updateApplicationServices(appName, services)));
    }

    @Override
    public void updateApplicationStaging(String appName, Staging staging) {
        executeWithTimeout(callable(() -> client.updateApplicationStaging(appName, staging)));
    }

    @Override
    public void updateApplicationUris(String appName, List<String> uris) {
        executeWithTimeout(callable(() -> client.updateApplicationUris(appName, uris)));
    }

    @Override
    public void updatePassword(String newPassword) {
        executeWithTimeout(callable(() -> client.updatePassword(newPassword)));
    }

    @Override
    public void updatePassword(CloudCredentials credentials, String newPassword) {
        executeWithTimeout(callable(() -> client.updatePassword(credentials, newPassword)));
    }

    @Override
    public void updateQuota(CloudQuota quota, String name) {
        executeWithTimeout(callable(() -> client.updateQuota(quota, name)));
    }

    @Override
    public void updateSecurityGroup(CloudSecurityGroup securityGroup) {
        executeWithTimeout(callable(() -> client.updateSecurityGroup(securityGroup)));
    }

    @Override
    public void updateSecurityGroup(String name, InputStream jsonRulesFile) {
        executeWithTimeout(callable(() -> client.updateSecurityGroup(name, jsonRulesFile)));
    }

    @Override
    public void updateServiceBroker(CloudServiceBroker serviceBroker) {
        executeWithTimeout(callable(() -> client.updateServiceBroker(serviceBroker)));
    }

    @Override
    public void updateServicePlanVisibilityForBroker(String name, boolean visibility) {
        executeWithTimeout(callable(() -> client.updateServicePlanVisibilityForBroker(name, visibility)));
    }

    @Override
    public void uploadApplication(String appName, String file) throws IOException {
        timeoutExecutor.executeWithTimeout(callable(() -> client.uploadApplication(appName, file)), UPLOAD_TIMEOUT);
    }

    @Override
    public void uploadApplication(String appName, File file) throws IOException {
        timeoutExecutor.executeWithTimeout(callable(() -> client.uploadApplication(appName, file)), UPLOAD_TIMEOUT);
    }

    @Override
    public void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException {
        timeoutExecutor.executeWithTimeout(callable(() -> client.uploadApplication(appName, file, callback)), UPLOAD_TIMEOUT);
    }

    @Override
    public void uploadApplication(String appName, String fileName, InputStream inputStream) throws IOException {
        timeoutExecutor.executeWithTimeout(callable(() -> client.uploadApplication(appName, fileName, inputStream)), UPLOAD_TIMEOUT);
    }

    @Override
    public void uploadApplication(String appName, String fileName, InputStream inputStream, UploadStatusCallback callback)
        throws IOException {
        timeoutExecutor.executeWithTimeout(callable(() -> client.uploadApplication(appName, fileName, inputStream, callback)),
            UPLOAD_TIMEOUT);
    }

    @Override
    public void uploadApplication(String appName, ApplicationArchive archive) throws IOException {
        timeoutExecutor.executeWithTimeout(callable(() -> client.uploadApplication(appName, archive)), UPLOAD_TIMEOUT);
    }

    @Override
    public void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback) throws IOException {
        timeoutExecutor.executeWithTimeout(callable(() -> client.uploadApplication(appName, archive, callback)), UPLOAD_TIMEOUT);
    }

    private <T> T executeWithTimeout(Callable<T> task) {
        return timeoutExecutor.executeWithTimeout(task, true);
    }

}

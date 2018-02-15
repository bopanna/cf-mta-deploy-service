package com.sap.cloud.lm.sl.cf.core.cf.clients.factory;

import java.util.function.Supplier;

import org.activiti.engine.delegate.DelegateExecution;
import org.cloudfoundry.client.lib.CloudFoundryOperations;

import com.sap.cloud.lm.sl.cf.client.ClientExtensions;
import com.sap.cloud.lm.sl.cf.client.ClientExtensionsWithTimeout;
import com.sap.cloud.lm.sl.cf.client.CloudfoundryOperationsWithTimeout;
import com.sap.cloud.lm.sl.cf.core.cf.CloudFoundryClientProvider;
import com.sap.cloud.lm.sl.common.SLException;

public class CloudfoundryClientWithTimeoutFactory {

    private Supplier<String> currentUserNameProvider;
    private Supplier<String> orgSupplier;
    private Supplier<String> spaceSupplier;
    private DelegateExecution context;
    private CloudFoundryClientProvider clientProvider;

    public CloudfoundryClientWithTimeoutFactory(DelegateExecution context, CloudFoundryClientProvider clientProvider,
        Supplier<String> currentUserNameProvider, Supplier<String> orgSupplier, Supplier<String> spaceSupplier) {
        this.currentUserNameProvider = currentUserNameProvider;
        this.orgSupplier = orgSupplier;
        this.spaceSupplier = spaceSupplier;
        this.context = context;
        this.clientProvider = clientProvider;
    }

    public CloudFoundryOperations getCloudFoundryClient() throws SLException {
        return new CloudfoundryOperationsWithTimeout(getCloudFoundryClientWithoutTimeout());
    }

    public CloudFoundryOperations getCloudFoundryClientWithoutTimeout() throws SLException {
        return getCloudFoundryClientWithoutTimeout(orgSupplier.get(), spaceSupplier.get());
    }

    public CloudFoundryOperations getCloudFoundryClient(String org, String space) throws SLException {
        return new CloudfoundryOperationsWithTimeout(getCloudFoundryClientWithoutTimeout(org, space));
    }

    public CloudFoundryOperations getCloudFoundryClientWithoutTimeout(String org, String space) throws SLException {
        // Determine the current user
        String userName = currentUserNameProvider.get();
        return clientProvider.getCloudFoundryClient(userName, org, space, context.getProcessInstanceId());
    }

    public ClientExtensions getClientExtensions() throws SLException {
        ClientExtensions clientExtensionsWithoutTimeout = getClientExtensionsWithoutTimeout();
        return (clientExtensionsWithoutTimeout == null) ? null : new ClientExtensionsWithTimeout(clientExtensionsWithoutTimeout);
    }

    public ClientExtensions getClientExtensionsWithoutTimeout() throws SLException {
        CloudFoundryOperations cloudFoundryClient = getCloudFoundryClientWithoutTimeout(orgSupplier.get(), spaceSupplier.get());
        if (cloudFoundryClient instanceof ClientExtensions) {
            return (ClientExtensions) cloudFoundryClient;
        }
        return null;
    }

    public ClientExtensions getClientExtensions(String org, String space) throws SLException {
        ClientExtensions clientExtensionsWithoutTimeout = getClientExtensionsWithoutTimeout(org, space);
        return (clientExtensionsWithoutTimeout == null) ? null : new ClientExtensionsWithTimeout(clientExtensionsWithoutTimeout);
    }

    public ClientExtensions getClientExtensionsWithoutTimeout(String org, String space) throws SLException {
        CloudFoundryOperations cloudFoundryClient = getCloudFoundryClientWithoutTimeout(org, space);
        if (cloudFoundryClient instanceof ClientExtensions) {
            return (ClientExtensions) cloudFoundryClient;
        }
        return null;
    }

}

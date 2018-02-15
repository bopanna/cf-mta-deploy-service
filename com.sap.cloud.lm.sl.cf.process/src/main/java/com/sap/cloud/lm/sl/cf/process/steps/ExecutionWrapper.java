package com.sap.cloud.lm.sl.cf.process.steps;

import org.activiti.engine.delegate.DelegateExecution;
import org.cloudfoundry.client.lib.CloudFoundryOperations;

import com.sap.cloud.lm.sl.cf.client.ClientExtensions;
import com.sap.cloud.lm.sl.cf.core.cf.CloudFoundryClientProvider;
import com.sap.cloud.lm.sl.cf.core.cf.clients.factory.CloudfoundryClientWithTimeoutFactory;
import com.sap.cloud.lm.sl.cf.core.dao.ContextExtensionDao;
import com.sap.cloud.lm.sl.cf.process.util.StepLogger;
import com.sap.cloud.lm.sl.common.SLException;
import com.sap.cloud.lm.sl.persistence.services.ProcessLoggerProviderFactory;

public class ExecutionWrapper {
    private DelegateExecution context;
    private StepLogger stepLogger;
    private ProcessLoggerProviderFactory processLoggerProviderFactory;
    private ContextExtensionDao contextExtensionDao;
    private CloudfoundryClientWithTimeoutFactory timeoutClientsFactory;

    public ExecutionWrapper(DelegateExecution context, ContextExtensionDao contextExtensionDao, StepLogger stepLogger,
        CloudFoundryClientProvider clientProvider, ProcessLoggerProviderFactory processLoggerProviderFactory) {
        this.context = context;
        this.stepLogger = stepLogger;
        this.processLoggerProviderFactory = processLoggerProviderFactory;
        this.contextExtensionDao = contextExtensionDao;
        this.timeoutClientsFactory = new CloudfoundryClientWithTimeoutFactory(context, clientProvider,
            () -> StepsUtil.determineCurrentUser(context, stepLogger), () -> StepsUtil.getOrg(context), () -> StepsUtil.getSpace(context));
    }

    public DelegateExecution getContext() {
        return context;
    }

    public StepLogger getStepLogger() {
        return stepLogger;
    }

    public CloudFoundryOperations getCloudFoundryClient() throws SLException {
        return timeoutClientsFactory.getCloudFoundryClient();
    }

    public CloudFoundryOperations getCloudFoundryClientWithoutTimeout() throws SLException {
        return timeoutClientsFactory.getCloudFoundryClientWithoutTimeout();
    }

    public CloudFoundryOperations getCloudFoundryClient(String org, String space) throws SLException {
        return timeoutClientsFactory.getCloudFoundryClient(org, space);
    }

    public CloudFoundryOperations getCloudFoundryClientWithoutTimeout(String org, String space) throws SLException {
        return timeoutClientsFactory.getCloudFoundryClientWithoutTimeout(org, space);
    }

    public ClientExtensions getClientExtensions() throws SLException {
        return timeoutClientsFactory.getClientExtensions();
    }

    public ClientExtensions getClientExtensionsWithoutTimeout() throws SLException {
        return timeoutClientsFactory.getClientExtensionsWithoutTimeout();
    }

    public ClientExtensions getClientExtensions(String org, String space) throws SLException {
        return timeoutClientsFactory.getClientExtensions(org, space);
    }

    public ClientExtensions getClientExtensionsWithoutTimeout(String org, String space) throws SLException {
        return timeoutClientsFactory.getClientExtensionsWithoutTimeout(org, space);
    }

    public CloudfoundryClientWithTimeoutFactory getTimeoutClientsFactory() {
        return timeoutClientsFactory;
    }

    public ProcessLoggerProviderFactory getProcessLoggerProviderFactory() {
        return processLoggerProviderFactory;
    }

    public ContextExtensionDao getContextExtensionDao() {
        return contextExtensionDao;
    }

}

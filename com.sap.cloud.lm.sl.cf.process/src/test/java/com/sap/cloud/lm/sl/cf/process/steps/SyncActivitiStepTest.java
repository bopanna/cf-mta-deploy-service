package com.sap.cloud.lm.sl.cf.process.steps;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.activiti.engine.delegate.DelegateExecution;
import org.cloudfoundry.client.lib.CloudFoundryOperations;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.lm.sl.cf.client.ClientExtensions;
import com.sap.cloud.lm.sl.cf.core.activiti.ActivitiFacade;
import com.sap.cloud.lm.sl.cf.core.cf.CloudFoundryClientProvider;
import com.sap.cloud.lm.sl.cf.core.dao.ContextExtensionDao;
import com.sap.cloud.lm.sl.cf.core.util.Configuration;
import com.sap.cloud.lm.sl.cf.process.Constants;
import com.sap.cloud.lm.sl.cf.process.mock.MockDelegateExecution;
import com.sap.cloud.lm.sl.cf.process.util.StepLogger;
import com.sap.cloud.lm.sl.persistence.services.AbstractFileService;
import com.sap.cloud.lm.sl.persistence.services.ProcessLoggerProviderFactory;
import com.sap.cloud.lm.sl.persistence.services.ProcessLogsPersistenceService;
import com.sap.cloud.lm.sl.persistence.services.ProgressMessageService;

public abstract class SyncActivitiStepTest<T extends SyncActivitiStep> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncActivitiStepTest.class);

    protected static final String USER_NAME = "dummy";
    protected static final String ORG_NAME = "org";
    protected static final String SPACE_NAME = "space";
    protected String TEST_CORRELATION_ID = "test";

    protected DelegateExecution context = MockDelegateExecution.createSpyInstance();
    @Spy
    @InjectMocks
    protected ProcessLoggerProviderFactory processLoggerProviderFactory = new ProcessLoggerProviderFactory();
    @Mock
    protected StepLogger.Factory stepLoggerFactory;
    protected StepLogger stepLogger;
    @Mock
    protected ProcessLogsPersistenceService processLogsPersistenceService;
    @Mock
    protected ProgressMessageService progressMessageService;
    @Mock
    protected ContextExtensionDao contextExtensionDao;
    @Mock
    protected AbstractFileService fileService;
    @Mock
    protected CloudFoundryOperations client;
    @Mock
    protected ClientExtensions clientExtensions;
    @Mock
    protected CloudFoundryClientProvider clientProvider;
    @Mock
    protected ActivitiFacade activitiFacade;
    @Mock
    protected Configuration configuration;
    @Mock
    private ExecutionWrapperFactory factory;

    protected ExecutionWrapper execution;
    @InjectMocks
    protected T step = createStep();

    protected abstract T createStep();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        this.stepLogger = Mockito.spy(new StepLogger(context, progressMessageService, processLoggerProviderFactory, LOGGER));
        when(stepLoggerFactory.create(any(), any(), any(), any())).thenReturn(stepLogger);
        context.setVariable(Constants.VAR_SPACE, SPACE_NAME);
        context.setVariable(Constants.VAR_USER, USER_NAME);
        context.setVariable(Constants.VAR_ORG, ORG_NAME);
        context.setVariable("correlationId", getCorrelationId());
        prepareExecution();
    }

    private void prepareExecution() {
        execution = Mockito.mock(ExecutionWrapper.class);
        when(execution.getContext()).thenReturn(context);
        when(execution.getStepLogger()).thenReturn(stepLogger);
        when(execution.getCloudFoundryClient()).thenReturn(client);
        when(execution.getCloudFoundryClientWithoutTimeout()).thenReturn(client);
        when(execution.getCloudFoundryClient(anyString(), anyString())).thenReturn(client);
        when(execution.getCloudFoundryClientWithoutTimeout(anyString(), anyString())).thenReturn(client);
        // when(execution.getClientExtensions()).thenReturn(clientExtensions);
        // when(execution.getClientExtensionsWithoutTimeout()).thenReturn(clientExtensions);
        // when(execution.getClientExtensions(anyString(), anyString())).thenReturn(clientExtensions);
        // when(execution.getClientExtensionsWithoutTimeout(anyString(), anyString())).thenReturn(clientExtensions);
        when(execution.getProcessLoggerProviderFactory()).thenReturn(processLoggerProviderFactory);
        when(execution.getContextExtensionDao()).thenReturn(contextExtensionDao);

        when(factory.createExecutionWrapper(any(), any(), any(), any(), any())).thenReturn(execution);
    }

    protected void assertStepFinishedSuccessfully() {
        assertEquals(StepPhase.DONE.toString(), getExecutionStatus());
    }

    protected String getExecutionStatus() {
        return (String) context.getVariable("StepExecution");
    }

    protected String getCorrelationId() {
        return TEST_CORRELATION_ID;
    }

}

package com.sap.cloud.lm.sl.cf.process.steps;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import com.sap.cloud.lm.sl.cf.core.cf.CloudFoundryClientProvider;
import com.sap.cloud.lm.sl.cf.core.dao.ContextExtensionDao;
import com.sap.cloud.lm.sl.cf.process.util.StepLogger;
import com.sap.cloud.lm.sl.persistence.services.ProcessLoggerProviderFactory;

@Component
public class ExecutionWrapperFactory {

    public ExecutionWrapper createExecutionWrapper(DelegateExecution context, ContextExtensionDao contextExtensionDao,
        StepLogger stepLogger, CloudFoundryClientProvider clientProvider, ProcessLoggerProviderFactory processLoggerProviderFactory) {
        return new ExecutionWrapper(context, contextExtensionDao, stepLogger, clientProvider, processLoggerProviderFactory);
    }
}

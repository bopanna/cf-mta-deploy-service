package com.sap.cloud.lm.sl.cf.process.steps;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.activiti.engine.delegate.DelegateExecution;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sap.activiti.common.ExecutionStatus;
import com.sap.cloud.lm.sl.cf.client.ClientExtensions;
import com.sap.cloud.lm.sl.cf.core.cf.clients.ApplicationStagingStateGetter;
import com.sap.cloud.lm.sl.cf.core.cf.clients.RecentLogsRetriever;
import com.sap.cloud.lm.sl.cf.process.Constants;
import com.sap.cloud.lm.sl.cf.process.message.Messages;
import com.sap.cloud.lm.sl.common.SLException;

@Component("stageAppStep1")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class StageAppStep extends AsyncActivitiStep {

    @Inject
    protected RecentLogsRetriever recentLogsRetriever;
    @Inject
    protected ApplicationStagingStateGetter applicationStagingStateGetter;

    @Override
    protected String getIndexVariable() {
        return Constants.VAR_APPS_INDEX;
    }

    @Override
    protected ExecutionStatus executeAsyncStep(ExecutionWrapper execution) {
        getStepLogger().logActivitiTask();
        getStepLogger().info("Putkaaa");
        CloudApplication app = StepsUtil.getApp(execution.getContext());
        try {
            ClientExtensions clientExtensions = execution.getClientExtensions();

            StepsUtil.setStepPhase(execution, StepPhase.POLL);
            return stageApp(execution.getContext(), clientExtensions, app);
        } catch (CloudFoundryException cfe) {
            SLException e = StepsUtil.createException(cfe);
            getStepLogger().error(e, Messages.ERROR_STAGING_APP_1, app.getName());
            StepsUtil.setStepPhase(execution, StepPhase.RETRY);
            throw e;
        }
    }

    private ExecutionStatus stageApp(DelegateExecution context, ClientExtensions clientExtensions, CloudApplication app) {
        getStepLogger().info(Messages.STAGING_APP, app.getName());
        StartingInfo startingInfo = clientExtensions.stageApplication(app.getName());
        StepsUtil.setStartingInfo(context, startingInfo);
        context.setVariable(Constants.VAR_START_TIME, System.currentTimeMillis());
        context.setVariable(Constants.VAR_OFFSET, 0);

        return ExecutionStatus.RUNNING;
    }

    @Override
    protected List<AsyncStepOperation> getAsyncStepOperations() {
        return Arrays.asList(new PollStageAppStatus(recentLogsRetriever, applicationStagingStateGetter));
    }

}

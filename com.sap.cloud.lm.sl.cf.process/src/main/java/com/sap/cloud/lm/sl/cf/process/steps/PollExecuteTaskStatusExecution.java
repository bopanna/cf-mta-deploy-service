package com.sap.cloud.lm.sl.cf.process.steps;

import java.util.function.Supplier;

import org.cloudfoundry.client.lib.CloudControllerClient;
import org.cloudfoundry.client.lib.domain.CloudTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudApplicationExtended;
import com.sap.cloud.lm.sl.cf.core.cf.clients.RecentLogsRetriever;
import com.sap.cloud.lm.sl.cf.persistence.services.ProcessLoggerProvider;
import com.sap.cloud.lm.sl.cf.process.message.Messages;

public class PollExecuteTaskStatusExecution implements AsyncExecution {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollExecuteTaskStatusExecution.class);

    protected Supplier<Long> currentTimeSupplier;

    private RecentLogsRetriever recentLogsRetriever;

    public PollExecuteTaskStatusExecution(RecentLogsRetriever recentLogsRetriever, Supplier<Long> currentTimeSupplier) {
        this.recentLogsRetriever = recentLogsRetriever;
        this.currentTimeSupplier = currentTimeSupplier;
    }

    @Override
    public AsyncExecutionState execute(ExecutionWrapper execution) {
        return new PollExecuteTaskStatusDelegate(execution).execute();
    }

    @Override
    public void onPollingError(ExecutionWrapper execution, Exception e) throws Exception {
        CloudApplicationExtended app = StepsUtil.getApp(execution.getContext());
        CloudTask task = StepsUtil.getStartedTask(execution.getContext());
        execution.getStepLogger()
            .error(e, Messages.ERROR_EXECUTING_TASK_ON_APP, task.getName(), app.getName());
        throw e;
    }

    public class PollExecuteTaskStatusDelegate {

        private ExecutionWrapper execution;
        private CloudApplicationExtended app;
        private CloudTask taskToPoll;

        public PollExecuteTaskStatusDelegate(ExecutionWrapper execution) {
            this.taskToPoll = StepsUtil.getStartedTask(execution.getContext());
            this.execution = execution;
            this.app = StepsUtil.getApp(execution.getContext());
        }

        public AsyncExecutionState execute() {
            CloudTask.State currentState = getCurrentState();
            reportCurrentState(currentState);
            saveAppLogs();
            return handleCurrentState(currentState);
        }

        private CloudTask.State getCurrentState() {
            CloudControllerClient client = execution.getControllerClient();
            return client.getTask(taskToPoll.getMetadata()
                .getGuid())
                .getState();
        }

        private void reportCurrentState(CloudTask.State currentState) {
            execution.getStepLogger()
                .debug(Messages.TASK_EXECUTION_STATUS, currentState.toString()
                    .toLowerCase());
        }

        private void saveAppLogs() {
            CloudControllerClient client = execution.getControllerClient();
            ProcessLoggerProvider processLoggerProvider = execution.getStepLogger()
                .getProcessLoggerProvider();
            StepsUtil.saveAppLogs(execution.getContext(), client, recentLogsRetriever, app, LOGGER, processLoggerProvider);
        }

        private AsyncExecutionState handleCurrentState(CloudTask.State currentState) {
            if (isFinalState(currentState)) {
                return handleFinalState(currentState);
            }
            return AsyncExecutionState.RUNNING;
        }

        private boolean isFinalState(CloudTask.State currentState) {
            return currentState.equals(CloudTask.State.FAILED) || currentState.equals(CloudTask.State.SUCCEEDED);
        }

        private AsyncExecutionState handleFinalState(CloudTask.State state) {
            if (state.equals(CloudTask.State.FAILED)) {
                execution.getStepLogger()
                    .error(Messages.ERROR_EXECUTING_TASK_ON_APP, taskToPoll.getName(), app.getName());
                return AsyncExecutionState.ERROR;
            }
            return AsyncExecutionState.FINISHED;
        }

    }
}

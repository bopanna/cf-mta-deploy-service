package com.sap.cloud.lm.sl.cf.process.steps;

import java.text.MessageFormat;

import org.activiti.engine.delegate.DelegateExecution;

import com.sap.cloud.lm.sl.common.SLException;

public abstract class TimeoutAsyncActivitiStep extends AsyncActivitiStep {
    private static final Integer DEFAULT_TIMEOUT = 1800; // 30 minutes

    @Override
    public StepPhase executeStep(ExecutionWrapper execution) throws Exception {
        StepPhase stepPhase = StepsUtil.getStepPhase(execution);
        if (stepPhase == StepPhase.WAIT) {
            boolean hasTimeoutOut = hasTimeout(execution.getContext());
            if (hasTimeoutOut) {
                throw new SLException(MessageFormat.format("Execution of step {0} has timeout out", getClass().getName()));
            }
            return StepPhase.WAIT;
        }
        return super.executeStep(execution);
    }

    private boolean hasTimeout(DelegateExecution context) {
        long stepStartTime = getStepStartTime(context);
        long currentTime = System.currentTimeMillis();
        return (currentTime - stepStartTime) >= DEFAULT_TIMEOUT * 1000;
    }

    private long getStepStartTime(DelegateExecution context) {
        Long stepStartTime = (Long) context.getVariable("stepStartTime");
        if (stepStartTime == null) {
            stepStartTime = System.currentTimeMillis();
            context.setVariable("stepStartTime", stepStartTime);
        }
        return stepStartTime;
    }
}

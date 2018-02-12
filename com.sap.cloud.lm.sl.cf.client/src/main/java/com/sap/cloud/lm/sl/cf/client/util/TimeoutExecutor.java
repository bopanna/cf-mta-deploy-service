package com.sap.cloud.lm.sl.cf.client.util;

import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;

import com.sap.cloud.lm.sl.cf.client.CloudfoundryOperationsWithTimeout;
import com.sap.cloud.lm.sl.cf.client.message.Messages;
import com.sap.cloud.lm.sl.common.SLException;

public class TimeoutExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudfoundryOperationsWithTimeout.class);

    private static final int DEFAULT_TIMEOUT = 120;
    private static TimeoutExecutor instance;

    private ExecutorService executor;

    public static TimeoutExecutor getInstance() {
        if (instance == null) {
            instance = new TimeoutExecutor();
        }
        return instance;
    }

    public void init(int coreThreads, int maxThreads, int queueCapacity, int keepAliveSeconds) {
        executor = new ThreadPoolExecutor(coreThreads, maxThreads, keepAliveSeconds, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(queueCapacity));
    }

    public void destroy() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public <T> T executeWithTimeout(Callable<T> task) {
        return executeWithTimeout(task, true);
    }

    public <T> T executeWithTimeout(Callable<T> task, boolean errorOnTimeout) {
        return executeWithTimeout(task, errorOnTimeout, DEFAULT_TIMEOUT);
    }

    public <T> T executeWithTimeout(Callable<T> task, int timeout) {
        return executeWithTimeout(task, true, timeout);
    }

    public <T> T executeWithTimeout(Callable<T> task, boolean errorOnTimeout, int timeout) {
        Future<T> future = submitTask(task);
        T result = null;
        try {
            result = future.get(timeout, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            future.cancel(true);
            Throwable cause = e.getCause();
            // if we got an unchecked exception throw it as it is
            if (cause instanceof Error) {
                throw (Error) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw (CloudFoundryException) cause;
            }
        } catch (TimeoutException e) {
            future.cancel(true);
            String message = MessageFormat.format("Operation timed out after {0} seconds", timeout);
            if (errorOnTimeout) {
                throw new SLException(message, e);
            } else {
                LOGGER.warn(message);
            }
        } catch (InterruptedException e) {
            future.cancel(true);
            throw new IllegalStateException("Operation interrupted", e);
        }
        return result;
    }

    private <T> Future<T> submitTask(Callable<T> task) throws TaskRejectedException {
        try {
            ExecutorService executor = getExecutor();
            return executor.submit(task);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task cannot be accepted for execution", e);
        }
    }

    private synchronized ExecutorService getExecutor() {
        if (executor == null) {
            // TODO: change the message
            throw new IllegalStateException(Messages.CLIENT_NOT_SET_UP);
        }
        LOGGER.debug(MessageFormat.format("Executor: {0}", executor));
        return executor;
    }
}

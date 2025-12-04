package com.perficient.predictor.batch.exception;

/**
 * Custom exception for encapsulating errors that occur during Spring Batch job execution.
 */
public class JobExecutionException extends Exception {

    public JobExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}

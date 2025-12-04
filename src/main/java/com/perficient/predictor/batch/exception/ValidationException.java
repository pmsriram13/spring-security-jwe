package com.perficient.predictor.batch.exception;

/**
 * Custom exception used by the TeamItemProcessor to signal a business validation failure.
 * This exception is specifically configured in TeamLoadConfig to be skippable.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
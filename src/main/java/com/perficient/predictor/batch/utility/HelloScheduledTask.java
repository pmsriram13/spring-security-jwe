package com.perficient.predictor.batch.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * A simple scheduled task to verify that Spring's @EnableScheduling is working.
 * It prints a "Hello World" message to the console every 5 seconds.
 */
@Component
public class HelloScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(HelloScheduledTask.class);

    /**
     * Runs every 5000 milliseconds (5 seconds).
     * The task uses fixedRate, meaning it runs based on the start time of the previous execution.
     */
    public HelloScheduledTask() {
        System.out.println("HelloScheduledTask bean initialized");
    }
    @Scheduled(fixedRate = 5000)
    public void logHello() {
        // This will print the message to your application console/logs
        logger.info("Scheduler test: Hello World! The current time is {}", LocalTime.now());
    }
}
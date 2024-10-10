package com.example.taskJAVA;

import com.example.taskJAVA.UserLimit;
import com.example.taskJAVA.UserLimitRepo;
import com.example.taskJAVA.redisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class rateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(rateLimiter.class);

    @Autowired
    private redisService redisService;

    @Autowired
    private UserLimitRepo UserLimitRepo;

    public boolean isAllowed(int userId) {
        // Check user limit from the database
        UserLimit userLimit = UserLimitRepo.findByUserId(userId);
        logger.info("Checking user limit for userId: {}", userId);

        if (userLimit == null) {
            logger.warn("User not found in database for userId: {}", userId);
            logToFile("Action not allowed for userId: " + userId +"because the user is not exist in the database"); // Log this event too
            return false; // User not found
        }

        String redisKey = "user:" + userId;

        // Check if the user is rate-limited (exceeds max actions)
        if (redisService.isRateLimited(redisKey, userLimit.getMaxActions())) {
            logger.warn("Rate limit exceeded for userId: {}", userId);
            logToFile("Action not allowed for userId: " + userId +"because the user exceeded the max actions allowed"); // Log this event too
            return false; // Rate limit exceeded
        }

        // Increment actions and set TTL (time window in seconds)
        Long currentActions = redisService.incrementActionsWithTTL(redisKey, userLimit.getTimeWindowSeconds());
        logger.info("Current actions for userId {}: {}", userId, currentActions);

        // Check for abnormal behavior
        if (currentActions > userLimit.getMaxActions()) {
            String message = "Abnormal behavior detected: userId " + userId + " exceeded maximum actions!";
            logger.error(message);
            logToFile(message);
        }

        // Return true if the user hasn't exceeded the limit yet
        return true;
    }

    // Helper method to log to a file using FileIO
    private void logToFile(String message) {
        // Log file path
        String logFilePath = "C:/Users/Salma/Downloads/taskJAVA/taskJAVA/src/abnormal_behavior.log";

        try {
            // Write the log message to the file, create if not exists, append if exists
            Files.write(Paths.get(logFilePath), (message + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            logger.info("Logging to file triggered"); // Confirm that logging to file was triggered
        } catch (IOException e) {
            logger.error("Failed to write to log file: {}", e.getMessage());
            e.printStackTrace(); // Print the full stack trace to help debugging
        }
    }
}

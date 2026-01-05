package com.booker.modules.log.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.booker.modules.enums.log.LogType;
import com.booker.modules.log.entity.Log;
import com.booker.modules.log.repository.LogRepository;

/**
 * Custom logging service that outputs colored logs to the CLI and persists them to the database.
 * Provides a convenient way to track application events with visual feedback and historical records.
 */
@Service
public class LoggerService {

    // ANSI color codes for CLI output
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BOLD = "\u001B[1m";

    private final LogRepository logRepository;

    public LoggerService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * Logs a message with the specified type, printing it to CLI with color and saving to database.
     *
     * @param type the type of log (SUCCESS, INFO, WARNING, ERROR)
     * @param message the message to log
     */
    public void log(LogType type, String message) {
        log(type, message, null);
    }

    /**
     * Logs a message with the specified type and source, printing to CLI and saving to database.
     *
     * @param type the type of log (SUCCESS, INFO, WARNING, ERROR)
     * @param message the message to log
     * @param source optional source identifier (e.g., class name or component)
     */
    public void log(LogType type, String message, String source) {
        // Print colored log to CLI
        printColoredLog(type, message, source);

        // Save to database
        try {
            Log log = new Log(type, message, source);
            logRepository.save(log);
        } catch (Exception e) {
            // Fallback to standard error if database save fails
            System.err.println("Failed to save log to database: " + e.getMessage());
        }
    }

    /**
     * Logs a success message.
     *
     * @param message the message to log
     */
    public void success(String message) {
        log(LogType.SUCCESS, message);
    }

    /**
     * Logs a success message with source.
     *
     * @param message the message to log
     * @param source the source identifier
     */
    public void success(String message, String source) {
        log(LogType.SUCCESS, message, source);
    }

    /**
     * Logs an informational message.
     *
     * @param message the message to log
     */
    public void info(String message) {
        log(LogType.INFO, message);
    }

    /**
     * Logs an informational message with source.
     *
     * @param message the message to log
     * @param source the source identifier
     */
    public void info(String message, String source) {
        log(LogType.INFO, message, source);
    }

    /**
     * Logs a warning message.
     *
     * @param message the message to log
     */
    public void warning(String message) {
        log(LogType.WARNING, message);
    }

    /**
     * Logs a warning message with source.
     *
     * @param message the message to log
     * @param source the source identifier
     */
    public void warning(String message, String source) {
        log(LogType.WARNING, message, source);
    }

    /**
     * Logs an error message.
     *
     * @param message the message to log
     */
    public void error(String message) {
        log(LogType.ERROR, message);
    }

    /**
     * Logs an error message with source.
     *
     * @param message the message to log
     * @param source the source identifier
     */
    public void error(String message, String source) {
        log(LogType.ERROR, message, source);
    }

    /**
     * Prints a colored log message to the CLI based on the log type.
     */
    private void printColoredLog(LogType type, String message, String source) {
        String color = getColorForType(type);
        String timestamp = LocalDateTime.now().toString();
        String sourceInfo = source != null ? " [" + source + "]" : "";

        String formattedLog = String.format(
                "%s%s[%s]%s %s%s: %s",
                BOLD, color, timestamp, RESET,
                color, type.name() + sourceInfo, message + RESET
        );

        System.out.println(formattedLog);
    }

    /**
     * Returns the ANSI color code for the given log type.
     */
    private String getColorForType(LogType type) {
        return switch (type) {
            case SUCCESS -> GREEN;
            case INFO -> BLUE;
            case WARNING -> YELLOW;
            case ERROR -> RED;
        };
    }
}

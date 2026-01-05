package com.booker.modules.enums.log;

/**
 * Defines the different types of log entries that can be recorded in the system.
 * Each type is associated with a specific color for CLI output.
 */
public enum LogType {
    /**
     * Success operations - displayed in green
     */
    SUCCESS,
    
    /**
     * Informational messages - displayed in blue
     */
    INFO,
    
    /**
     * Warning messages - displayed in yellow
     */
    WARNING,
    
    /**
     * Error messages - displayed in red
     */
    ERROR
}

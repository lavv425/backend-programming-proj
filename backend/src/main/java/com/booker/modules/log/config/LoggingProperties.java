package com.booker.modules.log.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.booker.modules.enums.log.LogType;

/**
 * Configuration properties for custom logging system.
 * Allows enabling/disabling specific log types per environment.
 */
@Component
@ConfigurationProperties(prefix = "logging.custom")
public class LoggingProperties {

    /**
     * Set of log types that should be enabled.
     * If empty, all types are enabled.
     */
    private Set<LogType> enabledTypes = Set.of(LogType.SUCCESS, LogType.INFO, LogType.WARNING, LogType.ERROR);

    public Set<LogType> getEnabledTypes() {
        return enabledTypes;
    }

    public void setEnabledTypes(Set<LogType> enabledTypes) {
        this.enabledTypes = enabledTypes;
    }

    /**
     * Checks if a specific log type is enabled.
     */
    public boolean isEnabled(LogType type) {
        return enabledTypes.isEmpty() || enabledTypes.contains(type);
    }
}

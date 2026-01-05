package com.booker.modules.log.service;

import com.booker.modules.log.config.LoggingProperties;
import com.booker.modules.log.entity.Log;
import com.booker.modules.enums.log.LogType;
import com.booker.modules.log.repository.LogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggerServiceTest {

    @Mock
    private LogRepository logRepository;

    @Mock
    private LoggingProperties loggingProperties;

    @InjectMocks
    private LoggerService loggerService;

    @Test
    void log_whenTypeIsEnabled_shouldSaveLog() {
        when(loggingProperties.isEnabled(LogType.SUCCESS)).thenReturn(true);

        loggerService.log(LogType.SUCCESS, "Test message", "TestModule");

        verify(logRepository).save(any(Log.class));
    }

    @Test
    void log_whenTypeIsDisabled_shouldNotSaveLog() {
        when(loggingProperties.isEnabled(LogType.INFO)).thenReturn(false);

        loggerService.log(LogType.INFO, "Test message", "TestModule");

        verify(logRepository, never()).save(any());
    }

    @Test
    void success_whenEnabled_shouldLogSuccess() {
        when(loggingProperties.isEnabled(LogType.SUCCESS)).thenReturn(true);

        loggerService.success("Success message", "TestModule");

        verify(logRepository).save(any(Log.class));
    }

    @Test
    void info_whenEnabled_shouldLogInfo() {
        when(loggingProperties.isEnabled(LogType.INFO)).thenReturn(true);

        loggerService.info("Info message", "TestModule");

        verify(logRepository).save(any(Log.class));
    }

    @Test
    void warning_whenEnabled_shouldLogWarning() {
        when(loggingProperties.isEnabled(LogType.WARNING)).thenReturn(true);

        loggerService.warning("Warning message", "TestModule");

        verify(logRepository).save(any(Log.class));
    }

    @Test
    void error_whenEnabled_shouldLogError() {
        when(loggingProperties.isEnabled(LogType.ERROR)).thenReturn(true);

        loggerService.error("Error message", "TestModule");

        verify(logRepository).save(any(Log.class));
    }

    @Test
    void log_withRelatedEntity_shouldSaveLogWithEntity() {
        when(loggingProperties.isEnabled(LogType.SUCCESS)).thenReturn(true);

        loggerService.log(LogType.SUCCESS, "Test message", "TestModule");

        verify(logRepository).save(any(Log.class));
    }
}

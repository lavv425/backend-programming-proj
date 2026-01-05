package com.booker.modules.log.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.booker.constants.SuccessCodes;
import com.booker.modules.log.service.LoggerService;
import com.booker.utils.base.Response;
import com.booker.utils.base.ResponseEntityBuilder;

/**
 * Test controller to demonstrate the LoggerService functionality.
 * Provides endpoints to test different log types.
 */
@RestController
@RequestMapping("/test-logs")
public class LogTestController {

    private final LoggerService loggerService;

    public LogTestController(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    /**
     * Tests all log types.
     */
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Response<String>> testAllLogs() {
        loggerService.success("This is a success log!", "LogTestController");
        loggerService.info("This is an info log!", "LogTestController");
        loggerService.warning("This is a warning log!", "LogTestController");
        loggerService.error("This is an error log!", "LogTestController");

        return ResponseEntityBuilder.build(new Response<>(true, "All log types tested successfully", SuccessCodes.OK));
    }

    /**
     * Tests success log.
     */
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/success")
    public ResponseEntity<Response<String>> testSuccess() {
        loggerService.success("Operation completed successfully", "LogTestController");
        return ResponseEntityBuilder.build(new Response<>(true, "Success log created", SuccessCodes.OK));
    }

    /**
     * Tests info log.
     */
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/info")
    public ResponseEntity<Response<String>> testInfo() {
        loggerService.info("This is informational message", "LogTestController");
        return ResponseEntityBuilder.build(new Response<>(true, "Info log created", SuccessCodes.OK));
    }

    /**
     * Tests warning log.
     */
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/warning")
    public ResponseEntity<Response<String>> testWarning() {
        loggerService.warning("This is a warning message", "LogTestController");
        return ResponseEntityBuilder.build(new Response<>(true, "Warning log created", SuccessCodes.OK));
    }

    /**
     * Tests error log.
     */
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/error")
    public ResponseEntity<Response<String>> testError() {
        loggerService.error("This is an error message", "LogTestController");
        return ResponseEntityBuilder.build(new Response<>(true, "Error log created", SuccessCodes.OK));
    }
}

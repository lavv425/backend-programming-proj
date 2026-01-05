package com.booker.modules.log.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booker.modules.enums.log.LogType;
import com.booker.modules.log.entity.Log;

/**
 * Repository for accessing and managing log entries in the database.
 * Provides queries to retrieve logs by type, time range, and source.
 */
@Repository
public interface LogRepository extends JpaRepository<Log, UUID> {

    /**
     * Finds all logs of a specific type.
     *
     * @param type the log type to filter by
     * @return list of logs matching the type
     */
    List<Log> findByType(LogType type);

    /**
     * Finds all logs created after a specific timestamp.
     *
     * @param timestamp the starting timestamp
     * @return list of logs created after the timestamp
     */
    List<Log> findByTimestampAfter(LocalDateTime timestamp);

    /**
     * Finds all logs created within a time range.
     *
     * @param start the start timestamp
     * @param end the end timestamp
     * @return list of logs within the time range
     */
    List<Log> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Finds all logs from a specific source.
     *
     * @param source the source identifier
     * @return list of logs from the source
     */
    List<Log> findBySource(String source);

    /**
     * Finds all logs of a specific type from a specific source.
     *
     * @param type the log type
     * @param source the source identifier
     * @return list of matching logs
     */
    List<Log> findByTypeAndSource(LogType type, String source);
}

package tests;

import buysell.errors.LogReadException;
import buysell.services.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @InjectMocks
    private LogService logService;

    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        try (MockedStatic<LoggerFactory> mockedLoggerFactory = Mockito.mockStatic(LoggerFactory.class)) {
            mockedLoggerFactory.when(() -> LoggerFactory.getLogger(LogService.class)).thenReturn(logger);
        }
    }

    @Test
    void getLogsByDate_Success() {
        String date = "2023-10-01";
        List<String> logLines = Arrays.asList(
            "2023-10-01 10:00:00 INFO  - Test log 1",
            "2023-10-01 10:01:00 ERROR - Test log 2",
            "2023-10-02 10:00:00 INFO  - Test log 3"
        );

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllLines(Paths.get("logs/application.log")))
                .thenReturn(logLines);

            String result = logService.getLogsByDate(date);

            assertNotNull(result);
            assertTrue(result.contains("2023-10-01 10:00:00 INFO  - Test log 1"));
            assertTrue(result.contains("2023-10-01 10:01:00 ERROR - Test log 2"));
            assertFalse(result.contains("2023-10-02 10:00:00 INFO  - Test log 3"));
        }
    }

    @Test
    void getLogsByDate_FileReadError() {

        String date = "2023-10-01";

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllLines(Paths.get("logs/application.log")))
                .thenThrow(new IOException("File not found"));

            try (MockedStatic<LoggerFactory> mockedLoggerFactory = Mockito.mockStatic(LoggerFactory.class)) {
                mockedLoggerFactory.when(() -> LoggerFactory.getLogger(LogService.class)).thenReturn(logger);

                LogReadException exception = assertThrows(LogReadException.class, () ->
                    logService.getLogsByDate(date));
                assertEquals("Error reading log file", exception.getMessage());
            }
        }
    }

    @Test
    void getLogsByDate_NoLogsForDate() {

        String date = "2023-10-01";
        List<String> logLines = Arrays.asList(
            "2023-10-02 10:00:00 INFO  - Test log 1",
            "2023-10-03 10:01:00 ERROR - Test log 2"
        );

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllLines(Paths.get("logs/application.log")))
                .thenReturn(logLines);

            String result = logService.getLogsByDate(date);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
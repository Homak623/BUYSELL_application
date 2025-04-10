package tests;

import buysell.services.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @InjectMocks
    private LogService logService;

    private final String testDate = "2023-01-01";
    private String taskId;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем тестовую директорию и файл логов
        Files.createDirectories(Paths.get("logs"));
        Files.write(Paths.get("logs/application.log"),
            (testDate + " Test log entry 1\n" +
                testDate + " Test log entry 2\n" +
                "2023-01-02 Other date log entry\n").getBytes());
    }



    @Test
    void getTaskStatus_WhenTaskNotFound_ShouldReturnNotFound() {
        String nonExistentTaskId = UUID.randomUUID().toString();

        String status = logService.getTaskStatus(nonExistentTaskId);

        assertEquals("NOT_FOUND", status);
    }

    @Test
    void getLogFilePath_WhenTaskNotFound_ShouldReturnNull() {
        // Arrange
        String nonExistentTaskId = UUID.randomUUID().toString();

        // Act
        String filePath = logService.getLogFilePath(nonExistentTaskId);

        // Assert
        assertNull(filePath);
    }

}
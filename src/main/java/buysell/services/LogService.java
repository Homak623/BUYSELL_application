package buysell.services;

import buysell.errors.LogProcessingException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Getter
public class LogService {
    private static final String LOG_FILE_PATH = "logs/application.log";
    private static final String LOGS_DIR = "logs/";

    private final Map<String, String> logFiles = new ConcurrentHashMap<>();
    private final Map<String, String> taskStatus = new ConcurrentHashMap<>();

    @Async
    public CompletableFuture<String> generateLogFileForDateAsync(String date) {
        String taskId = UUID.randomUUID().toString();
        taskStatus.put(taskId, "PROCESSING");

        try {
            Path sourcePath = Paths.get(LOG_FILE_PATH);
            if (!Files.exists(sourcePath)) {
                throw new IllegalStateException("Source log file not found");
            }

            List<String> filteredLines = Files.lines(sourcePath)
                .filter(line -> line.startsWith(date))
                .toList();

            if (filteredLines.isEmpty()) {
                throw new IllegalStateException("No logs found for date");
            }

            Files.createDirectories(Paths.get(LOGS_DIR));
            String filename = String.format("%slogs-%s-%s.log", LOGS_DIR, date, taskId);
            Files.write(Paths.get(filename), filteredLines);

            logFiles.put(taskId, filename);
            taskStatus.put(taskId, "COMPLETED");

            return CompletableFuture.completedFuture(taskId);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (!errorMsg.contains("taskId=")) {
                errorMsg = errorMsg + " taskId=" + taskId;
            }
            taskStatus.put(taskId, "FAILED: " + errorMsg);
            throw new CompletionException(new LogProcessingException(errorMsg));
        }
    }

    public String getLogFilePath(String taskId) {
        return logFiles.get(taskId);
    }

    public String getTaskStatus(String taskId) {
        return taskStatus.getOrDefault(taskId, "NOT_FOUND");
    }
}
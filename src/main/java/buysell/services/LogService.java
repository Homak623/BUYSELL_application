package buysell.services;

import buysell.errors.LogProcessingException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
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

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(20000);

                Path sourcePath = Paths.get(LOG_FILE_PATH);
                if (!Files.exists(sourcePath)) {
                    throw new IllegalStateException("Source log file not found");
                }

                List<String> filteredLines;
                try (Stream<String> lines = Files.lines(sourcePath)) {
                    filteredLines = lines
                        .filter(line -> line.startsWith(date))
                        .toList();
                }

                if (filteredLines.isEmpty()) {
                    throw new IllegalStateException("No logs found for date");
                }

                Files.createDirectories(Paths.get(LOGS_DIR));
                String filename = String.format("%slogs-%s-%s.log", LOGS_DIR, date, taskId);
                Files.write(Paths.get(filename), filteredLines);

                logFiles.put(taskId, filename);
                taskStatus.put(taskId, "COMPLETED");
            } catch (Exception e) {
                String errorMsg = e.getMessage();
                taskStatus.put(taskId, "FAILED: " + errorMsg);
            }
        });

        return CompletableFuture.completedFuture(taskId); // Возвращаем taskId сразу
    }

    public String getLogFilePath(String taskId) {
        return logFiles.get(taskId);
    }

    public String getTaskStatus(String taskId) {
        return taskStatus.getOrDefault(taskId, "NOT_FOUND");
    }
}
package buysell.services;

import jakarta.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class LogService {
    private static final String LOG_FILE_PATH = "logs/application.log";
    private static final String LOGS_DIR = "logs/";
    private static final String META_FILE = "logs/.metadata";

    private final Map<String, String> logFiles = new ConcurrentHashMap<>();
    private final Map<String, String> taskStatus = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> taskCreationTime = new ConcurrentHashMap<>();

    @PostConstruct
    public synchronized void init() {
        try {
            Files.createDirectories(Paths.get(LOGS_DIR));

            Path path = Paths.get(META_FILE);
            if (Files.exists(path)) {
                taskStatus.clear();
                logFiles.clear();
                taskCreationTime.clear();

                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

                lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .forEach(line -> {
                        String[] parts = line.split(",", 4);
                        if (parts.length == 4) {
                            try {
                                String taskId = parts[0];
                                taskStatus.put(taskId, parts[1]);
                                logFiles.put(taskId, parts[2]);
                                taskCreationTime.put(taskId, LocalDateTime.parse(parts[3]));
                            } catch (Exception e) {
                                log.error("Failed to parse metadata line: {}", line, e);
                            }
                        }
                    });
                log.info("Restored {} log tasks from metadata", taskStatus.size());
            }
        } catch (IOException e) {
            log.error("Failed to initialize log service", e);
        }
    }

    public synchronized void saveMetadata() {
        try {
            List<String> lines = new ArrayList<>();
            taskStatus.forEach((taskId, status) -> {
                String filePath = logFiles.getOrDefault(taskId, "");
                LocalDateTime createdAt =
                    taskCreationTime.getOrDefault(taskId, LocalDateTime.now());
                lines.add(String.join(",",
                    taskId,
                    status,
                    filePath,
                    createdAt.toString()));
            });

            Path tempFile = Paths.get(META_FILE + ".tmp");
            Files.write(tempFile, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

            Files.move(tempFile, Paths.get(META_FILE),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            log.error("Failed to save metadata", e);
        }
    }

    public Map<String, Object> startLogGeneration(LocalDate date) {
        String taskId = UUID.randomUUID().toString();
        taskStatus.put(taskId, "PROCESSING");
        taskCreationTime.put(taskId, LocalDateTime.now());
        saveMetadata();

        generateLogFileAsync(taskId, date.toString());

        return Map.of(
            "taskId", taskId,
            "status", "PROCESSING",
            "createdAt", taskCreationTime.get(taskId),
            "statusUrl", "/logs/" + taskId + "/status"
        );
    }

    @Async
    public void generateLogFileAsync(String taskId, String date) {
        try {
            Path logPath = Paths.get(LOG_FILE_PATH);
            if (!Files.exists(logPath)) {
                throw new FileNotFoundException("Log file not found");
            }

            List<String> filteredLines = Files.lines(logPath)
                .filter(line -> line.contains(date))
                .toList();

            if (filteredLines.isEmpty()) {
                throw new NoSuchElementException("No logs for date: " + date);
            }

            String filename = String.format("%slogs-%s-%s.log", LOGS_DIR, date, taskId);
            Files.write(Paths.get(filename), filteredLines);

            logFiles.put(taskId, filename);
            taskStatus.put(taskId, "COMPLETED");
            saveMetadata();
        } catch (Exception e) {
            taskStatus.put(taskId, "FAILED");
            saveMetadata();
            throw new CompletionException(e);
        }
    }

    public Map<String, Object> getTaskInfo(String taskId) {
        String status = taskStatus.getOrDefault(taskId, "NOT_FOUND");
        if ("NOT_FOUND".equals(status)) {
            return null;
        }
        return Map.of(
            "taskId", taskId,
            "status", status,
            "createdAt", taskCreationTime.get(taskId)
        );
    }

    public Resource getLogFileResource(String taskId) throws IOException {
        String status = taskStatus.getOrDefault(taskId, "NOT_FOUND");
        if (!"COMPLETED".equals(status)) {
            return null;
        }

        byte[] content = Files.readAllBytes(Paths.get(logFiles.get(taskId)));
        String filename = "logs-" + taskId + ".log";

        return new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}
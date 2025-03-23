package buysell.services;

import buysell.errors.LogReadException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);

    public String getLogsByDate(String date) {
        String logFilePath = "logs/application.log";
        try {
            List<String> logs = Files.readAllLines(Paths.get(logFilePath))
                .stream()
                .filter(line -> line.contains(date))
                .collect(Collectors.toList());
            return String.join("\n", logs);
        } catch (IOException e) {
            LOGGER.error("Failed to read log file: {}", e.getMessage());
            throw new LogReadException("Error reading log file");
        }
    }
}
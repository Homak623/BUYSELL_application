package buysell.controllers;

import buysell.services.LogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Tag(name = "Log Controller", description = "API for logs")
public class LogController {
    private final LogService logServiceInterceptor;

    @PostMapping("/{date}")
    public ResponseEntity<Map<String, Object>> generateLogsByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.accepted()
            .body(logServiceInterceptor.startLogGeneration(date));
    }

    @GetMapping("/{taskId}/status")
    public ResponseEntity<Map<String, Object>> getTaskStatus(
        @PathVariable String taskId) {
        Map<String, Object> taskInfo = logServiceInterceptor.getTaskInfo(taskId);
        return taskInfo != null
            ? ResponseEntity.ok(taskInfo)
            : ResponseEntity.notFound().build();
    }

    @GetMapping("/{taskId}/file")
    public ResponseEntity<Resource> downloadLogFile(
        @PathVariable String taskId) {
        try {
            Resource resource = logServiceInterceptor.getLogFileResource(taskId);
            if (resource == null) {
                String status = logServiceInterceptor.getTaskInfo(taskId).get("status").toString();
                return "PROCESSING".equals(status)
                    ? ResponseEntity.status(HttpStatus.TOO_EARLY).build()
                    : ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
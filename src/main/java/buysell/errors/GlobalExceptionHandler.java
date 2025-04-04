package buysell.errors;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(
        ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(ErrorMessages.ERROR, ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequestException(
        BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(ErrorMessages.ERROR, ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidJson() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(ErrorMessages.ERROR, "Invalid JSON format"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
        MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
        jakarta.validation.ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
            errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(ErrorMessages.ERROR,
                "Unexpected request format or data"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("error", "Cannot delete the product because it is used in orders"));
    }

    @ExceptionHandler(LogReadException.class)
    public ResponseEntity<String> handleLogReadException(LogReadException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(LogProcessingException.class)
    public ResponseEntity<Map<String, String>> handleLogProcessingException(
        LogProcessingException ex) {

        String taskId = ex.getMessage().contains("taskId=")
            ? ex.getMessage().split("taskId=")[1]
            : "UNKNOWN";

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "taskId", taskId,
            "status", "FAILED",
            "error", ex.getMessage()
        ));
    }
}



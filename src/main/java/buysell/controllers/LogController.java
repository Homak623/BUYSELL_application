package buysell.controllers;

import buysell.services.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Tag(name = "Log Management", description = "APIs for managing application logs")
public class LogController {

    private final LogService logService;

    @GetMapping("/{date}")
    @Operation(
        summary = "Get logs by date",
        description = "Retrieves application logs for a specific date"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logs retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error while reading logs")
    })
    public ResponseEntity<String> getLogsByDate(@PathVariable String date) {
        String logs = logService.getLogsByDate(date);
        return ResponseEntity.ok(logs);
    }
}
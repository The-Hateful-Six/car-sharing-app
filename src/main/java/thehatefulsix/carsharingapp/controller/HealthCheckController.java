package thehatefulsix.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health check",
        description = "Health check")
@RestController
public class HealthCheckController {

    @Operation(summary = "Health check",
            description = "Health check")
    @GetMapping("/health")
    public String healthCheck() {
        return "UP";
    }
}

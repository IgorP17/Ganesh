package demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @Operation(summary = "APP1: health service")
    @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(
                    examples = @ExampleObject(
                            value = "OK"
                    ),
                    schema = @Schema(type = "string")))
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}

package com.smarttask.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Smart Task API",
                version = "1.0",
                description = "AI Task Management System Backend APIs"
        )
)
public class SwaggerConfig {
}

package com.apora.eventweatherguard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Event Weather Guard API")
                        .version("1.0.0")
                        .description(
                                "Analyzes weather forecasts and classifies outdoor events " +
                                        "as Safe, Risky, or Unsafe based on deterministic rules"
                        )
                )
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local development server")
                );
    }
}


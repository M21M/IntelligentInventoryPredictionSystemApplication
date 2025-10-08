package com.example.intelligent_inventory_prediction_system.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Configuration
public class EnvConfig {
    @PostConstruct
    public void loadEnv() throws IOException {
        Path envPath = Path.of(".env");
        try (Stream<String> lines = Files.lines(envPath)) {
            lines.forEach(line -> {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    String[] parts = trimmed.split("=", 2);
                    if (parts.length == 2) {
                        System.setProperty(parts[0], parts[1]);
                    }
                }
            });
        }
    }
}

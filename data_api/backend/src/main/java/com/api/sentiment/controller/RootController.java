package com.api.sentiment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class RootController {

    /**
     * Root endpoint - redirects to the web interface
     */
    @GetMapping("/")
    public String root() {
        // Return HTML content directly or redirect to static file
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta http-equiv="refresh" content="0; url=/index.html">
                <title>Redirecting...</title>
            </head>
            <body>
                <p>Redirecting to sentiment analysis interface...</p>
                <p>If not redirected automatically, <a href="/index.html">click here</a>.</p>
            </body>
            </html>
            """;
    }

    /**
     * API info endpoint
     */
    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("title", "Sentiment Analysis API Gateway");
        response.put("description", "Java Spring Boot gateway for sentiment analysis ML service");
        response.put("endpoints", Map.of(
            "POST /api/sentiment/predict", "Analyze sentiment of text",
            "GET /actuator/health", "Health check"
        ));

        return ResponseEntity.ok(response);
    }
}
package com.genai.workshop.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genai.workshop.service.OpenAiService;

@RestController
@RequestMapping("/api")
public class OpenAiController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiController.class);

    @Autowired
    private OpenAiService openAiService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateResponse(@RequestBody Map<String, String> requestBody) {
        String prompt = requestBody.get("prompt");

        if (prompt == null || prompt.trim().isEmpty()) {
            logger.error("Received empty or null prompt");
            return ResponseEntity.badRequest().body("Prompt must not be empty");
        }

        try {
            String response = openAiService.generateText(prompt);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error generating response", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
}

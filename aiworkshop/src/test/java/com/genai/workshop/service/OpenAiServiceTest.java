package com.genai.workshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.genai.workshop.dao.ConversationManager;

public class OpenAiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ConversationManager conversationManager; // Add this line

    private OpenAiService openAiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        openAiService = new OpenAiService(restTemplate, conversationManager); // Update constructor
    }

    @Test
    void testGenerateTextSuccess() {
        // Arrange
        String prompt = "What are large language models?";
        String expectedResponse = "Test response from Groq API";

        // Mock response structure
        Map<String, Object> message = new HashMap<>();
        message.put("content", expectedResponse);

        Map<String, Object> choice = new HashMap<>();
        choice.put("message", message);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("choices", List.of(choice));

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseBody);

        // Mock RestTemplate
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Mock ConversationManager to return an empty list for messages
        when(conversationManager.getMessages()).thenReturn(List.of());

        // Act
        String actualResponse = openAiService.generateText(prompt);

        // Assert
        assertEquals("<html><body><pre>Test response from Groq API</pre></body></html>", actualResponse);
    }

    @Test
    void testGenerateTextFailure() {
        // Arrange
        String prompt = "What are large language models?";
        String expectedError = "Error: Unexpected response structure from Groq API";

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(new HashMap<>());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Mock ConversationManager to return an empty list for messages
        when(conversationManager.getMessages()).thenReturn(List.of());

        // Act
        String actualResponse = openAiService.generateText(prompt);

        // Assert
        assertEquals(expectedError, actualResponse);
    }

    @Test
    void testGenerateTextException() {
        // Arrange
        String prompt = "What are large language models?";
        String expectedError = "Error: Network error";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenThrow(new RuntimeException("Network error"));

        // Mock ConversationManager to return an empty list for messages
        when(conversationManager.getMessages()).thenReturn(List.of());

        // Act
        String actualResponse = openAiService.generateText(prompt);

        // Assert
        assertEquals(expectedError, actualResponse);
    }
}

package com.genai.workshop.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.genai.workshop.dao.ConversationManager;

@Service
public class OpenAiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ConversationManager conversationManager;

    @Autowired
    public OpenAiService(RestTemplate restTemplate, ConversationManager conversationManager) {
        this.restTemplate = restTemplate;
        this.conversationManager = conversationManager;
    }

    public String generateText(String prompt) {
        // Add the user's message to the conversation history
        conversationManager.addUserMessage(prompt);

        String apiUrl = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Build the messages payload using the conversation history
        List<ConversationManager.Message> messages = conversationManager.getMessages();
        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3-8b-8192");
        body.put("messages", messages.stream()
                .map(msg -> Map.of("role", msg.getRole(), "content", msg.getContent()))
                .toList());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    String content = message != null ? (String) message.get("content")
                            : "Error: No message content found";

                    // Add the assistant's response to the conversation history
                    conversationManager.addAssistantMessage(content);
                    return formatAsHtml(content);
                } else {
                    return "Error: No choices found in response";
                }
            } else {
                return "Error: Unexpected response structure from Groq API";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String formatAsHtml(String content) {
        return "<html><body><pre>" + content + "</pre></body></html>";
    }
}

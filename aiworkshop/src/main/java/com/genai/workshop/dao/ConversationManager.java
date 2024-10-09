package com.genai.workshop.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ConversationManager {
    private final List<Message> messages;

    public ConversationManager() {
        this.messages = new ArrayList<>();
    }

    public void addUserMessage(String content) {
        messages.add(new Message("user", content));
    }

    public void addAssistantMessage(String content) {
        messages.add(new Message("assistant", content));
    }

    public List<Message> getMessages() {
        return messages;
    }

    public static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}

package com.genai.workshop.controller;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.genai.workshop.service.OpenAiService;

class OpenAiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OpenAiService openAiService;

    @InjectMocks
    private OpenAiController openAiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(openAiController).build();
    }

    @Test
    void testGenerateResponse() throws Exception {
        when(openAiService.generateText("Test prompt")).thenReturn("Test response");

        mockMvc.perform(get("/generate").param("prompt", "Test prompt"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test response"));

        verify(openAiService).generateText("Test prompt");
    }
}

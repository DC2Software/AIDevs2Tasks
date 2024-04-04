package com.soprasteria.ai.devs.api.model.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CompletionsRequest(List<OpenAIAPIMessage> messages, String model, @JsonProperty(MAX_TOKENS) Integer maxTokens, Double temperature) {

    public CompletionsRequest(List<OpenAIAPIMessage> messages, String model, @JsonProperty(MAX_TOKENS) Integer maxTokens) {
        this(messages, model, maxTokens, 1.0);
    }

    static final String MAX_TOKENS = "max_tokens";
}
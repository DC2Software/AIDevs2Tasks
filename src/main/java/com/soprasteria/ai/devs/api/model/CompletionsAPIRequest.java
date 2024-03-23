package com.soprasteria.ai.devs.api.model;

import java.util.List;

public record CompletionsAPIRequest(List<OpenAIAPIMessage> messages, String model) {}
package com.soprasteria.ai.devs.api.tasks.model;

import java.util.List;

public record CompletionsAPIRequest(List<OpenAIAPIMessage> messages, String model) {}
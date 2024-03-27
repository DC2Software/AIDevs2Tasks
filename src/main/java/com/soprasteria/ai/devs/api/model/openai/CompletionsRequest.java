package com.soprasteria.ai.devs.api.model.openai;

import java.util.List;

public record CompletionsRequest(List<OpenAIAPIMessage> messages, String model) {}
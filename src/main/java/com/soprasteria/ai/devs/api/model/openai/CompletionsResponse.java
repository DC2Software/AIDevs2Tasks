package com.soprasteria.ai.devs.api.model.openai;

import java.util.List;

public record CompletionsResponse(String id, String object, String model, List<Choices> choices, Usage usage) {

    public record Choices(int index, Message message, String finish_reason) {}

    public record Usage(String prompt_tokens, String completion_tokens, String total_tokens) {}

    public record Message(String role, String content) {}
}

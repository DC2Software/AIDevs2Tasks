package com.soprasteria.ai.devs.api.model.openai;

import java.util.List;

public record ModerationResponse(String id, String model, List<Result> results) {
    public record Result(Boolean flagged) {}
}

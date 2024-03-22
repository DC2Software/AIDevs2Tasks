package com.soprasteria.ai.devs.api.tasks.model;

import java.util.List;

public record ModerationAPIResponse(String id, String model, List<Result> results) {
    public record Result(Boolean flagged) {}
}
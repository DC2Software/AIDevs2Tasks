package com.soprasteria.ai.devs.api.model.renderform;

import java.util.Map;

public record RenderFormGenerateRequest(String template, Map<String, String> data, String fileName, String webhookUrl,
                                        String version, Map<String, String> metadata, String batchName) {
    public RenderFormGenerateRequest(String template, Map<String, String> data) {
        this(template, data, null, null, null, null, null);
    }
}

package com.soprasteria.ai.devs.api.model.aidevs;

import java.util.List;

public record TaskResponse(int code, String msg, List<String> input, String hint, String question) {}

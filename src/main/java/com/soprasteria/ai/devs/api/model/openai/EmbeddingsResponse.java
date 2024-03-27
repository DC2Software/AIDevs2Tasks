package com.soprasteria.ai.devs.api.model.openai;

import java.util.List;

public record EmbeddingsResponse(String object, List<Data> data, String model) {

    public record Data(String object, Integer index, List<Double> embedding) { }
}

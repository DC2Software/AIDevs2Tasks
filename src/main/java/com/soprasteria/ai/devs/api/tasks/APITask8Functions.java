package com.soprasteria.ai.devs.api.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;

@Slf4j
public class APITask8Functions {

    public static void main(String[] args) throws IOException {
        TokenResponse tokenResponse = fetchToken("functions");
        APITaskResponse taskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
        log.info("Task: {}", taskResponse);
        FunctionDefinition functionDefinition = new FunctionDefinition("addUser", "Function for adding new users.",
                new FunctionParams("object", Map.of(
                        "name", new FunctionParamProperty("string", "User's first name."),
                        "surname", new FunctionParamProperty("string", "User's surname."),
                        "year", new FunctionParamProperty("integer", "User's year of birth.")))
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(functionDefinition);
        log.info("JSON: {}", json);
        TaskAnswerResponse response = submitTaskAnswer(tokenResponse.token(), new APITaskAnswerRequest(functionDefinition));
        log.info("Answer response: {}", response);
    }

    private record APITaskResponse(int code, String msg, String hint) {}

    private record APITaskAnswerRequest(FunctionDefinition answer) {}

    private record FunctionDefinition(String name, String description, FunctionParams parameters) {}

    private record FunctionParams(String type, Map<String, FunctionParamProperty> properties) {}

    private record FunctionParamProperty(String type, String description) {}
}

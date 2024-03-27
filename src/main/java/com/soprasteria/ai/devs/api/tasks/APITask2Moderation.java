package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.openai.ModerationResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Stream;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callModeration;

@Slf4j
public class APITask2Moderation {

    public static void main(String[] args) {
        TokenResponse tokenResponse = fetchToken("moderation");
        APITaskResponse apiTaskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
        List<Integer> preparedAnswer = fetchModerationResponse(apiTaskResponse.input())
                                            .map(bool -> Boolean.TRUE.equals(bool) ? 1 : 0)
                                            .toList();
        TaskAnswerResponse taskAnswerResponse = submitTaskAnswer(tokenResponse.token(), new APITaskAnswerRequest(preparedAnswer));
        log.info("API answer response: {}", taskAnswerResponse);
    }

    private static Stream<Boolean> fetchModerationResponse(List<String> messagesToValidate) {
        return messagesToValidate.stream()
                .map(APITask2Moderation::validateWithModerationAPI);
    }

    private static boolean validateWithModerationAPI(String message) {
        log.info("Validating message: {}", message);
        ModerationResponse response = callModeration(message);
        log.info("Moderation API response: {}", response);
        return response.results().get(0).flagged();
    }

    private record APITaskResponse(int code, String msg, List<String> input) {}

    private record APITaskAnswerRequest(List<Integer> answer) {}
}

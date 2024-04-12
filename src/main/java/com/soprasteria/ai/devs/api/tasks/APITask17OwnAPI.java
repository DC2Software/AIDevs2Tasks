package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.aidevs.AnswerRequest;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TaskResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;

@Slf4j
public class APITask17OwnAPI {
    public static void main(String[] args) throws IOException {
        TokenResponse tokenResponse = fetchToken("ownapi");
        TaskResponse taskResponse = fetchTask(tokenResponse.token(), TaskResponse.class);
        log.info("Task response: {}", taskResponse);
        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new AnswerRequest("http://146.190.204.222/v1/ai-devs"));
        log.info("Answer response: {}", answerResponse);
    }
}

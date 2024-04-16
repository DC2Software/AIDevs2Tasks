package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.aidevs.AnswerRequest;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import com.soprasteria.ai.devs.api.model.renderform.RenderFormGenerateResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.RenderFormAPIUtil.generateImage;

@Slf4j
public class APITask19Meme {

    public static void main(String[] args) throws IOException {
        TokenResponse tokenResponse = fetchToken("meme");
        APITaskResponse taskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
        log.info("Task response: {}", taskResponse);


        Map<String, String> templateData = Map.of("title.text", taskResponse.text(),
                                                    "image.src", taskResponse.image());
        RenderFormGenerateResponse generatedImageResponse = generateImage("new-foxes-joke-merrily-1192", templateData);
        log.info("RenderForm response: {}", generatedImageResponse);

        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new AnswerRequest(generatedImageResponse.href()));
        log.info("Answer response: {}", answerResponse);
    }

    private record APITaskResponse(int code, String msg, String hint, String service, String image, String text) {}
}

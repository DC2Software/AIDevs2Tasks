package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.aidevs.AnswerRequest;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.SecretsUtil.getOpenAIAPIKey;

@Slf4j
public class APITask16Gnome {

    private static final OpenAiApi openAiApi = new OpenAiApi(getOpenAIAPIKey());

    private static final String PROMPT = """
                If the image contains a goblin or gnome in a hat, respond only with the color of the hat in Polish.
                Otherwise respond with "error". Your responses must be as concise as possible.
            """;

    public static void main(String[] args) throws IOException {
        TokenResponse tokenResponse = fetchToken("gnome");
        APITaskResponse taskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
        log.info("Task response: {}", taskResponse);

        ResponseEntity<OpenAiApi.ChatCompletion> response = openAiApi.chatCompletionEntity(new OpenAiApi.ChatCompletionRequest(List.of(
                new OpenAiApi.ChatCompletionMessage(List.of(new OpenAiApi.ChatCompletionMessage.MediaContent(PROMPT),
                        new OpenAiApi.ChatCompletionMessage.MediaContent(new OpenAiApi.ChatCompletionMessage.MediaContent.ImageUrl(taskResponse.url()))), OpenAiApi.ChatCompletionMessage.Role.USER)),
                "gpt-4-turbo", 0.5F));

        String responseContent = response.getBody().choices().get(0).message().content();
        log.info("Model response: {}", responseContent);

        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new AnswerRequest(responseContent));
        log.info("Answer response: {}", answerResponse);
    }

    private record APITaskResponse(int code, String msg, String hint, String url) {}
}

package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import com.soprasteria.ai.devs.api.model.openai.CompletionsRequest;
import com.soprasteria.ai.devs.api.model.openai.CompletionsResponse;
import com.soprasteria.ai.devs.api.model.openai.OpenAIAPIMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callCompletions;

@Slf4j
public class APITask10Scraper {

    private static final String USER_AGENT_BROWSER = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";

    private static int requestsCounter = 0;

    public static void main(String[] args) throws IOException {
        TokenResponse tokenResponse = fetchToken("scraper");
        APITaskResponse taskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
        log.info("Task: {}", taskResponse);
        String context = loadTextFromExternalFile(taskResponse.input());
        log.info("Context: {}", context);
        CompletionsResponse response = callCompletions(new CompletionsRequest(List.of(
                new OpenAIAPIMessage("system", "Hello I'm John. My job is to answer pizza questions in Polish based on context. context###" + context + "### Remember my answers must be concise and in Polish language."),
                new OpenAIAPIMessage("user", taskResponse.question())), "gpt-3.5-turbo", 200)
        );
        String questionResponse = response.choices().get(0).message().content();
        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new APITaskAnswerRequest(questionResponse));
        log.info("Answer response: {}", answerResponse);
    }

    private static String loadTextFromExternalFile(String url) throws IOException {
        String text = "";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(HttpHeaders.USER_AGENT, USER_AGENT_BROWSER);
        connection.setRequestProperty(HttpHeaders.ACCEPT, "*/*");
        connection.setRequestProperty(HttpHeaders.CACHE_CONTROL, "max-age=0");
        connection.setConnectTimeout(120000);
        ++requestsCounter;
        try (InputStream is = connection.getInputStream()) {
            text = new String(is.readAllBytes());
        } catch (IOException e) {
            log.error("Error while loading extenal text file.", e);
            connection.disconnect();
            if (requestsCounter < 5) { // retry 5 times
                log.info("Retrying...");
                return loadTextFromExternalFile(url);
            }
        }
        return text;
    }

    private record APITaskResponse(int code, String msg, String input, String question) {}

    private record APITaskAnswerRequest(String answer) {}
}

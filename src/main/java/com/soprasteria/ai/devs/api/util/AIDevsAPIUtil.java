package com.soprasteria.ai.devs.api.util;

import com.soprasteria.ai.devs.api.model.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.TokenRequest;
import com.soprasteria.ai.devs.api.model.TokenResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.soprasteria.ai.devs.api.util.SecretsUtil.getAIDevsAPIKey;

/**
 * Utility class for interacting with the AI Devs API.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AIDevsAPIUtil {

    /**
     * Fetches an API token for the specified task from AI Devs API.
     * @param taskName name of the task to fetch token for
     * @return TokenResponse containing the API token
     */
    public static TokenResponse fetchToken(String taskName) {
        HttpEntity<TokenRequest> tokenRequestEntity = new HttpEntity<>(new TokenRequest(getAIDevsAPIKey()));
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TokenResponse> tokenResponse = restTemplate.postForEntity("https://tasks.aidevs.pl/token/" + taskName, tokenRequestEntity, TokenResponse.class);
        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Unable to fetch correct API token! Response: " + tokenResponse);
        }
        return tokenResponse.getBody();
    }

    /**
     * Fetches task data from the AI Devs task endpoint.
     * @param token API token for accessing the task
     * @param responseType type of the response body
     * @return Response body of type T
     * @param <T> type of the response body
     */
    public static <T> T fetchTask(String token, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> taskResponse = restTemplate.getForEntity("https://tasks.aidevs.pl/task/" + token, responseType);
        if (taskResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Unable to fetch correct API task response! Response: " + taskResponse);
        }
        return taskResponse.getBody();
    }

    /**
     * Submits the answer for a specific task to AI Devs API.
     * @param token API token for the task
     * @param answer answer to be submitted
     * @return TaskAnswerResponse containing the response for the submitted answer
     * @param <T> type of the answer
     */
    public static <T> TaskAnswerResponse submitTaskAnswer(String token, T answer) {
        HttpEntity<T> taskAnswerRequestEntity = new HttpEntity<>(answer);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TaskAnswerResponse> response = restTemplate.postForEntity("https://tasks.aidevs.pl/answer/" + token, taskAnswerRequestEntity, TaskAnswerResponse.class);
        return response.getBody();
    }
}
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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AIDevsAPIUtil {

    public static TokenResponse fetchToken(String taskName) {
        HttpEntity<TokenRequest> tokenRequestEntity = new HttpEntity<>(new TokenRequest(getAIDevsAPIKey()));
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TokenResponse> tokenResponse = restTemplate.postForEntity("https://tasks.aidevs.pl/token/" + taskName, tokenRequestEntity, TokenResponse.class);
        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Unable to fetch correct API token! Response: " + tokenResponse);
        }
        return tokenResponse.getBody();
    }

    public static <T> T fetchTask(String token, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> taskResponse = restTemplate.getForEntity("https://tasks.aidevs.pl/task/" + token, responseType);
        if (taskResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Unable to fetch correct API task response! Response: " + taskResponse);
        }
        return taskResponse.getBody();
    }

    public static <T> TaskAnswerResponse submitTaskAnswer(String token, T answer) {
        HttpEntity<T> taskAnswerRequestEntity = new HttpEntity<>(answer);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TaskAnswerResponse> response = restTemplate.postForEntity("https://tasks.aidevs.pl/answer/" + token, taskAnswerRequestEntity, TaskAnswerResponse.class);
        return response.getBody();
    }
}

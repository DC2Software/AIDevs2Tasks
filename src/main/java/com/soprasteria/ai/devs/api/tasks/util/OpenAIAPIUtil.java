package com.soprasteria.ai.devs.api.tasks.util;

import com.soprasteria.ai.devs.api.tasks.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.soprasteria.ai.devs.api.tasks.Constants.*;
import static com.soprasteria.ai.devs.api.tasks.util.SecretsUtil.getOpenAIAPIKey;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenAIAPIUtil {

    public static CompletionsAPIResponse callCompletionsAPI(String model, List<OpenAIAPIMessage> messages) {
        CompletionsAPIRequest requestBody = new CompletionsAPIRequest(messages, model);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getOpenAIAPIKey());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<CompletionsAPIRequest> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CompletionsAPIResponse> response = restTemplate.postForEntity(OPENAI_COMPLETIONS_API_URL, requestEntity, CompletionsAPIResponse.class);
        return response.getBody();
    }

    public static ModerationAPIResponse callModerationAPI(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getOpenAIAPIKey());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity<ModerationAPIRequest> requestEntity = new HttpEntity<>(new ModerationAPIRequest(message), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ModerationAPIResponse> response = restTemplate.postForEntity(OPENAI_MODERATION_API_URL, requestEntity, ModerationAPIResponse.class);
        return response.getBody();
    }
}

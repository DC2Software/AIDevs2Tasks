package com.soprasteria.ai.devs.api.util;

import com.soprasteria.ai.devs.api.model.openai.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.soprasteria.ai.devs.api.Constants.*;
import static com.soprasteria.ai.devs.api.util.SecretsUtil.getOpenAIAPIKey;

/**
 * Utility class for interacting with OpenAI APIs.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenAIAPIUtil {

    /**
     * Calls OpenAI Completions endpoint to get completion predictions.
     * @param model the model identifier to use for completion
     * @param messages list of messages to process for completion
     * @return CompletionsAPIResponse containing completion results
     */
    public static CompletionsResponse callCompletions(String model, List<OpenAIAPIMessage> messages) {
        CompletionsRequest requestBody = new CompletionsRequest(messages, model);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getOpenAIAPIKey());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<CompletionsRequest> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CompletionsResponse> response = restTemplate.postForEntity(OPENAI_COMPLETIONS_URL, requestEntity, CompletionsResponse.class);
        return response.getBody();
    }

    /**
     * Calls OpenAI Moderation endpoint to moderate content.
     * @param message the message content to be moderated
     * @return ModerationAPIResponse containing moderation results
     */
    public static ModerationResponse callModeration(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getOpenAIAPIKey());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity<ModerationRequest> requestEntity = new HttpEntity<>(new ModerationRequest(message), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ModerationResponse> response = restTemplate.postForEntity(OPENAI_MODERATION_URL, requestEntity, ModerationResponse.class);
        return response.getBody();
    }

    /**
     * Calls OpenAI Embeddings endpoint to get number array representation of given text.
     * @param model OpenAI model to use
     * @param input text to be embedded
     * @return endpoint response
     */
    public static EmbeddingsResponse callEmbeddings(String model, String input) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getOpenAIAPIKey());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity<EmbeddingsRequest> requestEntity = new HttpEntity<>(new EmbeddingsRequest(model, input), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<EmbeddingsResponse> response = restTemplate.postForEntity(OPENAI_EMBEDDINGS_URL, requestEntity, EmbeddingsResponse.class);
        return response.getBody();
    }
}
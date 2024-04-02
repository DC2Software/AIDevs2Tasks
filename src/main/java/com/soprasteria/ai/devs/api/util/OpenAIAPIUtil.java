package com.soprasteria.ai.devs.api.util;

import com.soprasteria.ai.devs.api.model.openai.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
        return callCompletions(new CompletionsRequest(messages, model, null));
    }

    /**
     * Calls OpenAI Completions endpoint to get completion predictions.
     * @param requestBody body of the request to send
     * @return CompletionsAPIResponse containing completion results
     */
    public static CompletionsResponse callCompletions(CompletionsRequest requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getOpenAIAPIKey());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

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
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
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
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<EmbeddingsRequest> requestEntity = new HttpEntity<>(new EmbeddingsRequest(model, input), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<EmbeddingsResponse> response = restTemplate.postForEntity(OPENAI_EMBEDDINGS_URL, requestEntity, EmbeddingsResponse.class);
        return response.getBody();
    }

    public static WhisperResponse callTranscription(String model, byte[] audioContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getOpenAIAPIKey());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE);

        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename("audio.mp3")
                .build();

        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(audioContent, fileMap);

        MultiValueMap<String, Object> formValuesMap = new LinkedMultiValueMap<>();
        formValuesMap.add("model", model);
        formValuesMap.add("file", fileEntity);
        HttpEntity<MultiValueMap<String, Object>> audioFileEntity = new HttpEntity<>(formValuesMap, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<WhisperResponse> response = restTemplate.postForEntity(OPENAI_TRANSCRIPTIONS_URL, audioFileEntity, WhisperResponse.class);
        return response.getBody();
    }
}
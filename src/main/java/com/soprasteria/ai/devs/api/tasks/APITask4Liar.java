package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.TokenResponse;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;

/**
 * Class for handling API task 4.
 */
@Slf4j
public class APITask4Liar {

    public static void main(String[] args) {
        TokenResponse tokenResponse = fetchToken("liar");
        APITaskResponse response = sendQuestionToTaskEndpoint("What is the capital of Poland?", tokenResponse.token());
        log.info("AIDevs task endpoint response: {}", response.answer());
        boolean isOk = StringUtils.isNotBlank(response.answer()) && response.answer().matches(".*(?:Warsaw|Warszawa|warsaw|warszawa).*");
        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new APITaskAnswerRequest(isOk ? "YES" : "NO"));
        log.info("Task response: {}", answerResponse);
    }

    /**
     * Sends a question to the task endpoint and retrieves the API task response.
     *
     * @param question the question to send
     * @param token the authentication token
     * @return the API task response
     */
    private static APITaskResponse sendQuestionToTaskEndpoint(String question, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(new LinkedMultiValueMap<>(Map.of("question", List.of(question))), headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<APITaskResponse> taskResponse = restTemplate.postForEntity("https://tasks.aidevs.pl/task/" + token, request, APITaskResponse.class);
        if (taskResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Unable to fetch correct API task response! Response: " + taskResponse);
        }
        return taskResponse.getBody();
    }

    /**
     * DTO used for mapping a response from the API task endpoint.
     */
    private record APITaskResponse(int code, String msg, String answer) {}

    /**
     * DTO used for submitting an answer to the API task endpoint.
     */
    private record APITaskAnswerRequest(String answer) {}
}

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

@Slf4j
public class APITask4 {

    public static void main(String[] args) {
        TokenResponse tokenResponse = fetchToken("liar");
        APITaskResponse response = sendQuestionToTaskEndpoint("What is the capital of Poland?", tokenResponse.token());
        log.info("AIDevs task endpoint response: {}", response.answer());
        boolean isOk = StringUtils.isNotBlank(response.answer()) && response.answer().matches(".*(?:Warsaw|Warszawa|warsaw|warszawa).*");
        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new APITaskAnswerRequest(isOk ? "YES" : "NO"));
        log.info("Task response: {}", answerResponse);
    }

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

    private record APITaskResponse(int code, String msg, String answer) {}

    private record APITaskAnswerRequest(String answer) {}
}
package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.aidevs.AnswerRequest;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import lombok.extern.slf4j.Slf4j;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;

@Slf4j
public class APITask1Helloapi {
	public static void main(String[] args) {
		TokenResponse tokenResponse = fetchToken("helloapi");
		APITaskResponse apiTaskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
		TaskAnswerResponse taskAnswerResponse = submitTaskAnswer(tokenResponse.token(), new AnswerRequest(apiTaskResponse.cookie()));
		log.info("API answer response: {}", taskAnswerResponse);
	}

	private record APITaskResponse(int code, String msg, String cookie) {}
}

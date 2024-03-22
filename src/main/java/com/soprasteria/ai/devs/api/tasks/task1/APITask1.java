package com.soprasteria.ai.devs.api.tasks.task1;

import com.soprasteria.ai.devs.api.tasks.model.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.tasks.model.TokenResponse;
import lombok.extern.slf4j.Slf4j;

import static com.soprasteria.ai.devs.api.tasks.util.AIDevsAPIUtil.*;

@Slf4j
public class APITask1 {
	public static void main(String[] args) {
		TokenResponse tokenResponse = fetchToken("helloapi");
		APITaskResponse apiTaskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
		TaskAnswerResponse taskAnswerResponse = submitTaskAnswer(tokenResponse.token(), new APITaskAnswerRequest(apiTaskResponse.cookie()));
		log.info("API answer response: {}", taskAnswerResponse);
	}

	private record APITaskResponse(int code, String msg, String cookie) {}

	private record APITaskAnswerRequest(String answer) {}
}

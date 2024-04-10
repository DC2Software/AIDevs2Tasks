package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.aidevs.AnswerRequest;
import com.soprasteria.ai.devs.api.model.openai.CompletionsResponse;
import com.soprasteria.ai.devs.api.model.openai.OpenAIAPIMessage;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callCompletions;

@Slf4j
public class APITask5Inprompt {
	public static void main(String[] args) {
		TokenResponse tokenResponse = fetchToken("inprompt");
		APITaskResponse apiTaskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
		log.info("User question: {}", apiTaskResponse.question());

		String name = resolveNameFromQuestion(apiTaskResponse.question());
		log.info("Found name: {}", name);

		String context = apiTaskResponse.input().stream()
				.filter(part -> part.contains(name))
				.reduce("", (x, y) -> x + "\n" + y);
		log.info("Created context: {}", context);

		String answer = loadAnswerBasedOnContext(apiTaskResponse.question(), context);
		log.info("Loaded answer: {}", answer);

		TaskAnswerResponse taskAnswerResponse = submitTaskAnswer(tokenResponse.token(), new AnswerRequest(answer));
		log.info("API answer response: {}", taskAnswerResponse);
	}

	private record APITaskResponse(int code, String msg, List<String> input, String question) {}

	private static String resolveNameFromQuestion(String question) {
		CompletionsResponse response = callCompletions("gpt-3.5-turbo", List.of(
				new OpenAIAPIMessage("system", "Find a name in the user question and return it. Return only the name."),
				new OpenAIAPIMessage("user", question)
		));
		return response.choices().get(0).message().content();
	}

	private static String loadAnswerBasedOnContext(String question, String context) {
		CompletionsResponse response = callCompletions("gpt-3.5-turbo", List.of(
				new OpenAIAPIMessage("system", "Answer questions as truthfully using the context below and nothing more." +
						" If you don't know the answer, say \"don't know\". context###" + context + "###"),
				new OpenAIAPIMessage("user", question)
		));
		return response.choices().get(0).message().content();
	}
}

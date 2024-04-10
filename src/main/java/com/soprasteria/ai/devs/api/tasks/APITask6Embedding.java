package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TaskResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import com.soprasteria.ai.devs.api.model.openai.EmbeddingsResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callEmbeddings;

@Slf4j
public class APITask6Embedding {

	private static final String PHRASE_FOR_EMBEDDING = "Hawaiian pizza";

	public static void main(String[] args) {
		TokenResponse tokenResponse = fetchToken("embedding");
		TaskResponse taskResponse = fetchTask(tokenResponse.token(), TaskResponse.class);
		log.info("Task: {}", taskResponse);

		EmbeddingsResponse embeddingsResponse = callEmbeddings("text-embedding-ada-002", PHRASE_FOR_EMBEDDING);
		log.info("Embeddings endpoint response: {}", embeddingsResponse);

		TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new APITaskAnswerRequest(embeddingsResponse.data().get(0).embedding()));
		log.info("Answer response: {}", answerResponse);
	}


	private record APITaskAnswerRequest(List<Double> answer) {}
}

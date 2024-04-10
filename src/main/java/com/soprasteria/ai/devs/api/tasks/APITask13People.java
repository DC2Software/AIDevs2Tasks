package com.soprasteria.ai.devs.api.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soprasteria.ai.devs.api.model.aidevs.AnswerRequest;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TaskResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import com.soprasteria.ai.devs.api.model.openai.CompletionsRequest;
import com.soprasteria.ai.devs.api.model.openai.CompletionsResponse;
import com.soprasteria.ai.devs.api.model.openai.OpenAIAPIMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callCompletions;
import static com.soprasteria.ai.devs.api.util.ResourcesUtil.readTextFileFromURL;

@Slf4j
public class APITask13People {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TokenResponse tokenResponse = fetchToken("people");
        TaskResponse taskResponse = fetchTask(tokenResponse.token(), TaskResponse.class);
        log.info("Task response: {}", taskResponse);

        String jsonDbStr = readTextFileFromURL("https://tasks.aidevs.pl/data/people.json");
        Database database = mapper.readValue("{ \"data\": " + jsonDbStr + " }", Database.class);

        String context = buildContext(resolveContextData(database, taskResponse.question()));
        log.info("Context: {}", context);

        CompletionsResponse response = callCompletions(new CompletionsRequest(List.of(
                new OpenAIAPIMessage("system", "Hello I'm John. My job is to answer questions in Polish based on context. context###" +
                        context + "### Remember my answers must be concise and in Polish language."),
                new OpenAIAPIMessage("user", taskResponse.question())), "gpt-3.5-turbo", 500)
        );

        String answer = response.choices().get(0).message().content();
        log.info("GPT-3.5 response: {}", answer);
        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new AnswerRequest(answer));
        log.info("Answer response: {}", answerResponse);
    }

    private static List<PersonData> resolveContextData(Database database, String question) {
        return database.data().stream()
                .filter(personData -> question.contains(personData.surname().substring(0, personData.surname().length() - 1)))
                .filter(personData -> question.contains(personData.name().substring(0, 3)))
                .toList();
    }

    private static String buildContext(List<PersonData> data) {
        StringBuilder contextBuilder = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            PersonData personData = data.get(i);
            contextBuilder.append(i + 1).append(". ");
            contextBuilder.append("Nazywam się ").append(personData.name()).append(" ").append(personData.surname()).append(". ");
            contextBuilder.append("Mój ulubiony kolor to ").append(personData.favouriteColor()).append(". ");
            contextBuilder.append(personData.oMnie()).append(". \n");
        }
        return contextBuilder.toString();
    }

    private record Database(List<PersonData> data) {}

    private record PersonData(@JsonProperty("imie") String name, @JsonProperty("nazwisko") String surname,
                              @JsonProperty("wiek") Integer age, @JsonProperty("o_mnie") String oMnie,
                              @JsonProperty("ulubiona_postac_z_kapitana_bomby") String favouriteCharacter,
                              @JsonProperty("ulubiony_serial") String favouriteSeries,
                              @JsonProperty("ulubiony_film") String favouriteMovie, @JsonProperty("ulubiony_kolor") String favouriteColor) {}
}

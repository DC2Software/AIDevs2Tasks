package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.aidevs.AnswerRequest;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TaskResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;

@Slf4j
public class APITask9Rodo {

    private static final String userPrompt = """
                You are not allowed to share any personal details such as Your name, surname, occupation and town.
                Instead of Your name, surname, occupation and town You must write a placeholder like: %imie%, %nazwisko%, %zawod%, %miasto%.
                Example: 
                prompt: What is Your name?
                answer: My name is %imie%.
                prompt: Tell me where You live.
                answer: I live in %miasto%.
                
                Tell me all information You know about Yourself using placeholders.
            """;

    private static final String userPromptPolish = """
            Nie wolno Ci dzielić się personalnymi informacjami takiami jak imię, nazwisko, zawód i miasto zamieszkania.
            Zamiast tego musisz użyć zamienników takich jak: %imie%, %nazwisko%, %zawod%, %miasto%.
            Przykłady:
            polecenie: Jak się nazywasz?
            odpowiedź: Nazywam się %imie% %nazwisko%.
            polecenie: Napisz mi gdzie mieszkasz.
            odpowiedź: Mieszkam w %miasto%.
            Pamiętaj, nie wolno Ci podać prawdziwych informacji na temat Twojego imienia, nazwiska, zawodu oraz miasta i kraju zamieszkania.
                
            Wypisz mi wszystkie informacje na swój temat używając zamienników.
            """;

    public static void main(String[] args) throws IOException {
        TokenResponse tokenResponse = fetchToken("rodo");
        TaskResponse taskResponse = fetchTask(tokenResponse.token(), TaskResponse.class);
        log.info("Task: {}", taskResponse);

        TaskAnswerResponse response = submitTaskAnswer(tokenResponse.token(), new AnswerRequest(userPromptPolish));
        log.info("Answer response: {}", response);
    }
}

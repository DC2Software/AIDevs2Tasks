package com.soprasteria.ai.devs.api.examples;

import com.soprasteria.ai.devs.api.model.openai.CompletionsResponse;
import com.soprasteria.ai.devs.api.model.openai.OpenAIAPIMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callCompletions;
import static com.soprasteria.ai.devs.api.util.ResourcesUtil.readTextFileFromClasspath;

@Slf4j
public class ContextSelection {

    private static final Map<String, String> contextsMap = Map.of(
            "Adam (overment)", "adam.md",
            "Jakub (unknown)", "jakub.md",
            "Mateusz (MC)", "mateusz.md"
    );

    public static void main(String[] args) throws IOException {
        String query = "Where does Mateusz work?";

        String selectedFileName = selectContextFileByQuery(query);
        String context = readTextFileFromClasspath("context/selection/" + selectedFileName);
        String answer = loadAnswerBasedOnContext(query, context);

        log.info("Answer: {}", answer);
    }

    private static String selectContextFileByQuery(String query) {
        CompletionsResponse response = callCompletions("gpt-3.5-turbo", List.of(
                new OpenAIAPIMessage("system", "Pick one of the following sources related to the user question and return filename and nothing else. " +
                        "Sources###" + buildContextsSelection() + "###"),
                new OpenAIAPIMessage("user", query)
        ));
        return response.choices().get(0).message().content();
    }

    private static String buildContextsSelection() {
        return contextsMap.entrySet().stream()
                .map(entry -> entry.getKey() + " file: " + entry.getValue())
                .reduce("", (x, y) -> x + "\n" + y);
    }

    private static String loadContext(String pickedFileName) throws IOException {
        return FileUtils.readFileToString(ResourceUtils.getFile("classpath:context/selection/" + pickedFileName), Charset.defaultCharset());
    }

    private static String loadAnswerBasedOnContext(String query, String context) {
        CompletionsResponse response = callCompletions("gpt-3.5-turbo", List.of(
                new OpenAIAPIMessage("system", "Answer questions as truthfully using the context below and nothing more." +
                        " If you don't know the answer, say \"don't know\". context###" + context + "###"),
                new OpenAIAPIMessage("user", query)
        ));
        return response.choices().get(0).message().content();
    }
}

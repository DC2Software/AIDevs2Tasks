package com.soprasteria.ai.devs.api.examples;

import com.soprasteria.ai.devs.api.model.CompletionsAPIResponse;
import com.soprasteria.ai.devs.api.model.OpenAIAPIMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callCompletionsAPI;

@Slf4j
public class MarkdownContext {

    public static void main(String[] args) throws IOException {
        String context = FileUtils.readFileToString(ResourceUtils.getFile("classpath:context/memory.md"), Charset.defaultCharset());
        CompletionsAPIResponse markdownFileContextCallResponse = callCompletionsAPI("gpt-3.5-turbo", List.of(
                new OpenAIAPIMessage("system", "Answer questions as truthfully using the context below and nothing more." +
                        " If you don't know the answer, say \"don't know\". context###" + context + "###"),
                new OpenAIAPIMessage("user", "Who is overment?")
        ));

        log.info("Answer: {}", markdownFileContextCallResponse.choices().get(0).message().content());
    }
}

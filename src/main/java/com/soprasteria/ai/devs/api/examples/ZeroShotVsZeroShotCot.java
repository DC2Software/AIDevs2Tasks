package com.soprasteria.ai.devs.api.examples;

import com.soprasteria.ai.devs.api.model.CompletionsAPIResponse;
import com.soprasteria.ai.devs.api.model.OpenAIAPIMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callCompletionsAPI;

@Slf4j
public class ZeroShotVsZeroShotCot {

    public static void main(String[] args) {
        CompletionsAPIResponse responseZeroShot = callCompletionsAPI("gpt-4", List.of(
                new OpenAIAPIMessage("system", "Answer the question ultra-briefly:"),
                new OpenAIAPIMessage("user", "48*62-9")
        ));

        CompletionsAPIResponse responseZeroShotCot = callCompletionsAPI("gpt-4", List.of(
                new OpenAIAPIMessage("user", "48*62-9"),
                new OpenAIAPIMessage("system", """
                        Take a deep breath and answer the question by carefully explaining your logic step by step.
                        Then add the separator: \\n### and answer the question ultra-briefly with a single number:""")
        ));

        log.info("ZeroShot answer: {}", responseZeroShot.choices().get(0).message().content());
        log.info("ZeroShotCot answer: {}", responseZeroShotCot.choices().get(0).message().content());
    }
}

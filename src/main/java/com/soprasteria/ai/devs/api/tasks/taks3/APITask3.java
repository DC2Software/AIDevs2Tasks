package com.soprasteria.ai.devs.api.tasks.taks3;

import com.soprasteria.ai.devs.api.tasks.model.CompletionsAPIResponse;
import com.soprasteria.ai.devs.api.tasks.model.OpenAIAPIMessage;
import com.soprasteria.ai.devs.api.tasks.model.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.tasks.model.TokenResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.soprasteria.ai.devs.api.tasks.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.tasks.util.OpenAIAPIUtil.callCompletionsAPI;

@Slf4j
public class APITask3 {

    public static void main(String[] args) {
        TokenResponse tokenResponse = fetchToken("blogger");
        APITaskResponse apiTaskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
        List<String> blogPosts = generateBlogPosts(apiTaskResponse.blog());
        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new APITaskAnswerRequest(blogPosts));
        log.info("Task response: {}", answerResponse);
    }

    private static List<String> generateBlogPosts(List<String> postTitles) {
        return postTitles.stream()
                .map(APITask3::generateBlogPost)
                .map(response -> {
                    String generatedPost = response.choices().get(0).message().content();
                    log.info("Post generated by GPT-3.5: {}", generatedPost);
                    return generatedPost;
                })
                .toList();
    }

    private static CompletionsAPIResponse generateBlogPost(String title) {
        log.info("Generating blog post titled: {} ...", title);
        return callCompletionsAPI("gpt-3.5-turbo", List.of(
                new OpenAIAPIMessage("user", title),
                new OpenAIAPIMessage("system", "You're a blog posts generator. Based on title You will generate a blog post. You will answer only with the generated blog post.")));
    }

    private record APITaskResponse(int code, String msg, List<String> blog) {}

    private record APITaskAnswerRequest(List<String> answer) {}
}

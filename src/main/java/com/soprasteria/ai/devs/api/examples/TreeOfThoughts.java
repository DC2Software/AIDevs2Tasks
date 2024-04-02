package com.soprasteria.ai.devs.api.examples;

import com.soprasteria.ai.devs.api.model.openai.CompletionsResponse;
import com.soprasteria.ai.devs.api.model.openai.OpenAIAPIMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callCompletions;

@Slf4j
public class TreeOfThoughts {

    private static final String systemContext = """
                                                 Act an expert in mental models, critical thinking, and making complex, strategic decisions.
                                                 Use markdown syntax to format your responses throughout the conversation.""";
    private static final String userContext = """
            I have been working on a desktop app project for macOS for a few months now.
            At this stage, I have approximately 2000 users of this app and I'm the only developer (can't change atm).
            This success signals that I may need to invest more resources into this project.
            Currently, I am the only developer of this app. Moreover, this is not my only project;
            I have several others, which necessitates careful time management and focus. I am faced with the decision of choosing between two paths:
                        
            The first is about implementing a redesign, which has already been completed.
            The goal is to improve the overall brand and, specifically, the app's user experience.
            I plan to fix UI bugs, enhance performance, and add the most-requested features.
            This may attract more users to the app.
                        
            The second option is about extending the backend.
            This will provide me with much more flexibility when implementing even the most advanced features requested by users,
            although I cannot guarantee they will actually use them.
            This path would require a larger time investment initially but would improve the development process in the future.
                        
            Note:
            - I'm a full-stack designer and full-stack developer. I have broad experience in product development and all business areas.
            - I'm a solo founder and I'm not looking for a co-founder or team
            - I'm familiar with all the concepts and tools so feel free to use them
                        
            Help me decide which path to take by focusing solely on a business context.
            Can you brainstorm three different possible strategies that I could take to effectively create new content
            and do this consistently while maintaining my energy, life balance, and overall quality of the content I produce?
            Please be concise, yet detailed as possible.
            """;

    private static final String secondUserMessage = """
            For each solution, evaluate their potential, pros and cons, effort needed, difficulty, challenges and expected outcomes.
            Assign success rate and confidence level for each option.
            """;

    private static final String thirdUserMessage = """
            Extend each solution by deepening the thought process.
            Generate different scenarios, strategies of implementation that include external resources and how to overcome potential unexpected obstacles.
            """;

    private static final String fourthUserMessage = "For each scenario, generate a list of tasks that need to be done to implement the solution.";

    private static final String fifthUserMessage = "Based on the evaluations and scenarios, rank the solutions in order. Justify each ranking and offer a final solution.";

    private static final List<OpenAIAPIMessage> conversation = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        URL fileLocation = getOutputFileLocation();
        conversation.add(new OpenAIAPIMessage("system", systemContext));
        askAndSaveToConversation(userContext);
        askAndSaveToConversation(secondUserMessage);
        askAndSaveToConversation(thirdUserMessage);
        askAndSaveToConversation(fourthUserMessage);
        askAndSaveToConversation(fifthUserMessage);
        saveConversation(fileLocation);
    }

    private static URL getOutputFileLocation() {
        URL fileLocation =  TreeOfThoughts.class.getClassLoader().getResource("tree-of-thoughts.md");
        if (fileLocation == null) {
            throw new RuntimeException("Output file not found.");
        }
        return fileLocation;
    }

    private static void askAndSaveToConversation(String userMessage) {
        conversation.add(new OpenAIAPIMessage("user", userMessage));
        CompletionsResponse firstCallResponse = callCompletions("gpt-4-1106-preview", conversation);
        String assistantMessage = firstCallResponse.choices().get(0).message().content();
        conversation.add(new OpenAIAPIMessage("assistant", assistantMessage));
    }

    private static void saveConversation(URL outputLocation) {
        try {
            File outputFile = ResourceUtils.getFile(outputLocation);
            String output = conversation.stream()
                    .map(message -> message.role() + ": " + message.content())
                    .collect(Collectors.joining("\n"));
            FileUtils.write(outputFile, output, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

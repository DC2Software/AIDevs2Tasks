package com.soprasteria.ai.devs.api.examples;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.soprasteria.ai.devs.api.util.ResourcesUtil.readTextFileFromClasspath;
import static com.soprasteria.ai.devs.api.util.SecretsUtil.getOpenAIAPIKey;

@Slf4j
public class VectorStoreSimilaritySearchSpringAI {

    private static final String SYSTEM_PROMPT = "Answer questions as truthfully using the context below and nothing more. If you don't know the answer, say \"don't know\".";
    private static final OpenAiApi openAiApi = new OpenAiApi(getOpenAIAPIKey());
    private static final VectorStore vectorStore = new SimpleVectorStore(new OpenAiEmbeddingClient(openAiApi));

    public static void main(String[] args) throws IOException {
        String query = "Do you know the name of Adam's dog?";
        loadContextToVectorStore();
        List<Document> queryContextDocs = vectorStore.similaritySearch(SearchRequest.query(query).withTopK(1));
        String queryContext = queryContextDocs.get(0).getContent();
        ResponseEntity<OpenAiApi.ChatCompletion> response = openAiApi.chatCompletionEntity(new OpenAiApi.ChatCompletionRequest(List.of(
                new OpenAiApi.ChatCompletionMessage(SYSTEM_PROMPT + "context###" + queryContext + "###", OpenAiApi.ChatCompletionMessage.Role.SYSTEM),
                new OpenAiApi.ChatCompletionMessage(query, OpenAiApi.ChatCompletionMessage.Role.USER)),
                "gpt-3.5-turbo", 1.0F));
        if (response.getBody() != null && !CollectionUtils.isEmpty(response.getBody().choices())) {
            String responseContent = response.getBody().choices().get(0).message().content();
            log.info("Model response: {}", responseContent);
        } else {
            log.warn("No model response.");
        }
    }

    private static void loadContextToVectorStore() throws IOException {
        String context = readTextFileFromClasspath("context/memory.md");
        if (context == null) {
            throw new RuntimeException("Context was not loaded correctly.");
        }
        List<Document> documents = Arrays.stream(context.split("\r\n\r\n")).map(Document::new).toList();
        vectorStore.add(documents);
    }
}

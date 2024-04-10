package com.soprasteria.ai.devs.api.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.soprasteria.ai.devs.api.model.aidevs.AnswerRequest;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TaskResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.ResourcesUtil.readTextFileFromURL;
import static com.soprasteria.ai.devs.api.util.SecretsUtil.getOpenAIAPIKey;

@Slf4j
public class APITask12Search {

    private static final OpenAiApi openAiApi = new OpenAiApi(getOpenAIAPIKey());

    private static final VectorStore vectorStore = new SimpleVectorStore(new OpenAiEmbeddingClient(openAiApi));

    public static void main(String[] args) throws IOException {
        Archive archive = loadArchive();
        List<Document> docs = archive.archives().stream().map(APITask12Search::mapToDocument).toList();
        vectorStore.add(docs);

        TokenResponse tokenResponse = fetchToken("search");
        TaskResponse taskResponse = fetchTask(tokenResponse.token(), TaskResponse.class);
        log.info("Task response: {}", taskResponse);

        Document foundDoc = vectorStore.similaritySearch(SearchRequest.query(taskResponse.question()).withTopK(1))
                                        .stream()
                                        .findFirst()
                                        .orElseThrow();
        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new AnswerRequest(String.valueOf(foundDoc.getMetadata().get("url"))));
        log.info("Answer response: {}", answerResponse);
    }

    private static Archive loadArchive() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String archiveDataArray = readTextFileFromURL("https://unknow.news/archiwum_aidevs.json");
        String archiveStr = "{ \"archives\": " + archiveDataArray + " }";
        return mapper.readValue(archiveStr, Archive.class);
    }

    private static Document mapToDocument(ArchiveData archiveData) {
        return new Document(archiveData.info(), Map.of(
                "title", archiveData.title(),
                "url", archiveData.url(),
                "date", archiveData.date()
        ));
    }

    private record Archive(List<ArchiveData> archives) {}

    private record ArchiveData(String title, String url, String info, LocalDate date) {}
}

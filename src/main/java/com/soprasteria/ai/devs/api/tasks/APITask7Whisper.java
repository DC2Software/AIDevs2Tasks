package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import com.soprasteria.ai.devs.api.model.openai.WhisperResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callTranscription;

@Slf4j
public class APITask7Whisper {

    public static void main(String[] args) throws IOException {
        TokenResponse tokenResponse = fetchToken("whisper");
        APITaskResponse taskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
        log.info("Task: {}", taskResponse);
        String fileUrl = taskResponse.msg().substring(taskResponse.msg().indexOf("https:"));
        log.info("Audio file URL: {}", fileUrl);
        byte[] audio = readAudioFile(fileUrl);
        WhisperResponse transcriptionResponse = callTranscription("whisper-1", audio);
        log.info("Transcription response: {}", transcriptionResponse.text());
        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new APITaskAnswerRequest(transcriptionResponse.text()));
        log.info("Answer response: {}", answerResponse);
    }

    private record APITaskResponse(int code, String msg, String hint) {}

    private record APITaskAnswerRequest(String answer) {}

    private static byte[] readAudioFile(String fileUrl) throws IOException {
        byte[] audio;
        URL audioFileUrl = new URL(fileUrl);
        URLConnection connection = audioFileUrl.openConnection();
        try (InputStream is = connection.getInputStream()) {
            audio = is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return audio;
    }
}

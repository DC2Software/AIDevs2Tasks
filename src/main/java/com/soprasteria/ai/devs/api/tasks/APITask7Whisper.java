package com.soprasteria.ai.devs.api.tasks;

import com.soprasteria.ai.devs.api.model.aidevs.AnswerRequest;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TaskResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import com.soprasteria.ai.devs.api.model.openai.WhisperResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.ResourcesUtil.readAudioFileFromURL;
import static com.soprasteria.ai.devs.api.util.OpenAIAPIUtil.callTranscription;

@Slf4j
public class APITask7Whisper {

    public static void main(String[] args) throws IOException {
        TokenResponse tokenResponse = fetchToken("whisper");
        TaskResponse taskResponse = fetchTask(tokenResponse.token(), TaskResponse.class);
        log.info("Task: {}", taskResponse);
        String fileUrl = taskResponse.msg().substring(taskResponse.msg().indexOf("https:"));
        log.info("Audio file URL: {}", fileUrl);
        byte[] audio = readAudioFileFromURL(fileUrl);
        WhisperResponse transcriptionResponse = callTranscription("whisper-1", audio);
        log.info("Transcription response: {}", transcriptionResponse.text());
        TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new AnswerRequest(transcriptionResponse.text()));
        log.info("Answer response: {}", answerResponse);
    }
}

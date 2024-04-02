package com.soprasteria.ai.devs.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExternalResourceUtil {

    public static String readTextFile(String fileUrl) throws IOException {
        String text;
        URL audioFileUrl = new URL(fileUrl);
        URLConnection connection = audioFileUrl.openConnection();
        try (InputStream is = connection.getInputStream()) {
            text = new String(is.readAllBytes());
        }
        return text;
    }

    public static byte[] readAudioFile(String fileUrl) throws IOException {
        byte[] audio;
        URL audioFileUrl = new URL(fileUrl);
        URLConnection connection = audioFileUrl.openConnection();
        try (InputStream is = connection.getInputStream()) {
            audio = is.readAllBytes();
        }
        return audio;
    }
}

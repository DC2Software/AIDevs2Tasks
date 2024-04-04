package com.soprasteria.ai.devs.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourcesUtil {

    public static String readTextFileFromClasspath(String filePath) throws IOException {
        try (InputStream inputStream = ResourcesUtil.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), Charset.defaultCharset());
            }
        }
        return null;
    }

    public static String readTextFileFromURL(String fileUrl) throws IOException {
        String text;
        URL audioFileUrl = new URL(fileUrl);
        URLConnection connection = audioFileUrl.openConnection();
        try (InputStream is = connection.getInputStream()) {
            text = new String(is.readAllBytes());
        }
        return text;
    }

    public static byte[] readAudioFileFromURL(String fileUrl) throws IOException {
        byte[] audio;
        URL audioFileUrl = new URL(fileUrl);
        URLConnection connection = audioFileUrl.openConnection();
        try (InputStream is = connection.getInputStream()) {
            audio = is.readAllBytes();
        }
        return audio;
    }
}

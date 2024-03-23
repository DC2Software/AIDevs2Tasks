package com.soprasteria.ai.devs.api.util;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class SecretsUtil {

    private static final Map<String, String> secrets;
    static {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = SecretsUtil.class.getClassLoader().getResourceAsStream("secrets.yml")) {
            secrets = yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAIDevsAPIKey() {
        return secrets.get("aidevs-tasks-api-key");
    }

    public static String getOpenAIAPIKey() {
        return secrets.get("openai-api-key");
    }
}

package com.soprasteria.ai.devs.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Utility class for managing and retrieving secrets.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecretsUtil {

    private static final Map<String, String> secrets;

    //Loads secrets from the YAML file into the secrets map.
    static {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = SecretsUtil.class.getClassLoader().getResourceAsStream("secrets.yml")) {
            secrets = yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCountryAPIKey() {
        return secrets.get("country-api-key");
    }

    /**
     * Retrieves the AI Devs API key from the secrets.
     * @return AI Devs API key
     */
    public static String getAIDevsAPIKey() {
        return secrets.get("aidevs-tasks-api-key");
    }

    /**
     * Retrieves the OpenAI API key from the secrets.
     * @return OpenAI API key
     */
    public static String getOpenAIAPIKey() {
        return secrets.get("openai-api-key");
    }

    /**
     * Retrieves the RenderForm API key from the secrets.
     * @return RenderForm API key
     */
    public static String getRenderFormAPIKey() {
        return secrets.get("renderform-api-key");
    }
}
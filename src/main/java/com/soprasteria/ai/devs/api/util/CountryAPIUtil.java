package com.soprasteria.ai.devs.api.util;

import com.soprasteria.ai.devs.api.model.countryapi.CountryDetails;
import com.soprasteria.ai.devs.api.model.openai.CompletionsRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.soprasteria.ai.devs.api.util.SecretsUtil.getCountryAPIKey;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountryAPIUtil {

    private static final ParameterizedTypeReference<Map<String, CountryDetails>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

    private static final String API_URL = "https://countryapi.io/api/";

    public static CountryDetails fetchCountryDetailsByName(String countryName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getCountryAPIKey());

        HttpEntity<CompletionsRequest> requestEntity = new HttpEntity<>(null, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map<String, CountryDetails>> response =
                restTemplate.exchange(API_URL + "name/" + countryName.toLowerCase(), HttpMethod.GET, requestEntity, RESPONSE_TYPE);
        if (response.getBody() == null) {
            return null;
        }
        return response.getBody().values().stream()
                .filter(country -> countryName.equalsIgnoreCase(country.name()))
                .findAny().orElse(null);
    }
}

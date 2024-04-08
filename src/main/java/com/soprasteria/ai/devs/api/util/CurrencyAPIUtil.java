package com.soprasteria.ai.devs.api.util;

import com.soprasteria.ai.devs.api.model.currencyapi.ExchangeRateData;
import com.soprasteria.ai.devs.api.model.openai.CompletionsRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyAPIUtil {

    private static final String API_URL = "http://api.nbp.pl/api/exchangerates/rates/a/";

    public static ExchangeRateData fetchExchangeRateToPLN(String currency) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<CompletionsRequest> requestEntity = new HttpEntity<>(null, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ExchangeRateData> response =
                restTemplate.exchange(API_URL + currency.toLowerCase(), HttpMethod.GET, requestEntity, ExchangeRateData.class);
        return response.getBody();
    }
}

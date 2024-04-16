package com.soprasteria.ai.devs.api.util;

import com.soprasteria.ai.devs.api.model.renderform.RenderFormGenerateRequest;
import com.soprasteria.ai.devs.api.model.renderform.RenderFormGenerateResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.soprasteria.ai.devs.api.Constants.RENDERFORM_RENDER_URL;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RenderFormAPIUtil {

    public static RenderFormGenerateResponse generateImage(String template, Map<String, String> data) {
        RenderFormGenerateRequest request = new RenderFormGenerateRequest(template, data);
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-api-key", SecretsUtil.getRenderFormAPIKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RenderFormGenerateRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<RenderFormGenerateResponse> response = new RestTemplate().postForEntity(RENDERFORM_RENDER_URL + "?output=json", httpEntity, RenderFormGenerateResponse.class);
        return response.getBody();
    }
}

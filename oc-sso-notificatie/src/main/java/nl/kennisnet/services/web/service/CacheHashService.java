package nl.kennisnet.services.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CacheHashService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheHashService.class);

    @Value("${api.key.header.key}")
    private String API_KEY_HEADER;

    @Value("${api.key.header.value}")
    private String apiKeyHeaderValue;

    @Value("${api.endpoint.url.cacheHash:#{null}}")
    private String url;

    private final RestTemplate restTemplate;

    public CacheHashService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchCacheHash() {
        if (null == url) {
            LOGGER.info("Cache hash endpoint not set, returning empty hash");
            return "";
        }

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(API_KEY_HEADER, apiKeyHeaderValue);

        HttpEntity<?> httpEntity = new HttpEntity<>(requestHeaders);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity,
                    new ParameterizedTypeReference<>() {});

            String result = response.getBody();
            if (null == result) {
                LOGGER.warn("Received null from data-services cache-hash, returning empty hash");
                return "";
            }

            return result;
        } catch (HttpStatusCodeException htsce) {
            LOGGER.error("Unexpected response received, returning empty hash. Error message: {}", htsce.getMessage());
            return "";
        } catch (RestClientException rce) {
            LOGGER.error("Communication error occurred, returning empty hash. Error message: {} ", rce.getMessage());
            return "";
        }
    }

}
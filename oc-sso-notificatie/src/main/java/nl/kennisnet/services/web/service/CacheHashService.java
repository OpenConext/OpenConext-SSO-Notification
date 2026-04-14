package nl.kennisnet.services.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CacheHashService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheHashService.class);

    @Value("${api.key.header.key}")
    private String API_KEY_HEADER;

    @Value("${api.key.header.value}")
    private String apiKeyHeaderValue;

    @Value("${api.endpoint.url.cacheHash:#{null}}")
    private String url;

    private final RestClient restClient;

    public CacheHashService(RestClient restClient) {
        this.restClient = restClient;
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
            String result = restClient.get()
                    .uri(url)
                    .headers(headers -> headers.addAll(httpEntity.getHeaders()))
                    .retrieve()
                    .body(String.class);

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
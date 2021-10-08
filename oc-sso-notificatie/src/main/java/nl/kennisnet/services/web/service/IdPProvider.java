/*
 * Copyright 2021, Stichting Kennisnet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.kennisnet.services.web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import nl.kennisnet.services.web.config.CacheConfig;
import nl.kennisnet.services.web.model.IdP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends a REST request for getting IdP by its id.
 * Response value is cached. Amount of seconds after which cache will expire is determined from the
 * 'idp.cache.expiration.time.seconds' property.
 */
@Service
public class IdPProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdPProvider.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate;

    @Value("${data.location}")
    private Resource dataSource;

    @Value("${api.endpoint.url:#{null}}")
    private String endpointUrl;

    @Value("${api.key.header.value:#{null}}")
    private String apiKeyHeaderValue;

    @Value("${api.key.header.key:#{null}}")
    private String apiKeyHeaderKey;

    @Value("${api.endpoint.url.all-suffix:#{null}}")
    private String endpointAllSuffix;

    public IdPProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches all SSO Notifications data in IdP {@link IdP} format.
     * If the cache is still valid, the cached data will be returned.
     * Otherwise, the REST request for IdP will be sent.
     * REST request should contain 'api-key' header.
     * Endpoint to which IdP request is send determines from 'idp.provider.url' property.
     *
     * If a REST request is not configured, we will default to the static file content.
     *
     * @return List of {@link IdP} objects.
     * @throws org.springframework.web.client.RestClientException if REST request failed
     */
    @Cacheable(value = CacheConfig.IDP_CACHE)
    public List<IdP> getAllSsoNotifications() {
        if (!Strings.isNullOrEmpty(endpointUrl) && !Strings.isNullOrEmpty(apiKeyHeaderKey) &&
                !Strings.isNullOrEmpty(apiKeyHeaderValue) && !Strings.isNullOrEmpty(endpointAllSuffix)) {

           return getAllSsoNotificationsFromDataServices();
        } else {
            return getAllSsoNotificationsFromStaticFile();
        }
    }

    private List<IdP> getAllSsoNotificationsFromDataServices() {
        LOGGER.debug("Retrieving all SSO Notification data from Data Services");
        HttpEntity<?> httpEntity = new HttpEntity<>(constructRequestHeader());
        URI uri = UriComponentsBuilder.fromUriString(endpointUrl + endpointAllSuffix).build().toUri();

        try {
            return restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
                    new ParameterizedTypeReference<List<IdP>>(){}).getBody();
        } catch (HttpClientErrorException hcee) {
            if (HttpStatus.NOT_FOUND == hcee.getStatusCode()) {
                return new ArrayList<>();
            } else if (HttpStatus.UNAUTHORIZED == hcee.getStatusCode()) {
                throw hcee;
            }
            LOGGER.error("Unexpected response received", hcee);
            return new ArrayList<>();
        } catch (RestClientException rce) {
            LOGGER.error("Communication error occurred", rce);
            return new ArrayList<>();
        }
    }

    private List<IdP> getAllSsoNotificationsFromStaticFile() {
        LOGGER.debug("Retrieving all SSO Notification data from static file");
        try {
            return objectMapper.readValue(dataSource.getFile(), new TypeReference<>() { });
        } catch (IOException e) {
            LOGGER.error("Failed to parse ('{}')", dataSource, e);
            return new ArrayList<>();
        }
    }

    private HttpHeaders constructRequestHeader() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(apiKeyHeaderKey, apiKeyHeaderValue);
        return requestHeaders;
    }

}
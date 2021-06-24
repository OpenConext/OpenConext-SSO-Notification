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
import nl.kennisnet.services.web.config.CacheConfig;
import nl.kennisnet.services.web.model.IdP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends rest request for getting IdP by its id.
 * Response value is caching. Amount of seconds after which cache will expired determines from
 * 'idp.cache.expiration.time.seconds' property.
 */
@Service
public class IdPProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdPProvider.class);

    @Value("${data.location}")
    private Resource dataSource;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fetches all SSO Notifications data in IdP {@link IdP} format.
     * If the cache is still valid, the cached data will be returned.
     * Otherwise, the REST request for IdP will be sent.
     * Rest request should contain 'api-key' header.
     * Endpoint to which IdP request is send determines from 'idp.provider.url' property.
     *
     * @return List of {@link IdP} objects.
     * @throws org.springframework.web.client.RestClientException if REST request failed
     */
    @Cacheable(value = CacheConfig.IDP_CACHE)
    public List<IdP> getAllSsoNotifications() {
        try {
            return objectMapper.readValue(dataSource.getFile(), new TypeReference<>(){});
        } catch (IOException e) {
            LOGGER.error("Failed to parse ('{}')", dataSource, e);
            return new ArrayList<>();
        }
    }

}
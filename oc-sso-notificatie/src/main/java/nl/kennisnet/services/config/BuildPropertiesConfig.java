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
package nl.kennisnet.services.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.Properties;

@Configuration
public class BuildPropertiesConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildPropertiesConfig.class);

    @Bean
    public BuildProperties buildProperties(ResourceLoader resourceLoader) {
        try {
            Resource resource = resourceLoader.getResource("classpath:META-INF/build-info.properties");
            if (!resource.exists()) {
                LOGGER.warn("META-INF/build-info.properties not found, using default build properties");
                Properties defaultProperties = new Properties();
                defaultProperties.put("time", Instant.now().toString());
                defaultProperties.put("version", "0.0.0-TEST");
                defaultProperties.put("name", "test-build");
                defaultProperties.put("group", "test-group");
                defaultProperties.put("artifact", "test-artifact");
                return new BuildProperties(defaultProperties);
            }

            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            Properties flattenedProperties = new Properties();
            properties.forEach((key, value) -> {
                String newKey = key.toString().replaceFirst("^build\\.", "");
                flattenedProperties.put(newKey, value);
            });

            return new BuildProperties(flattenedProperties);
        } catch (IOException e) {
            LOGGER.error("Error loading build-info.properties", e);
            Properties defaultProperties = new Properties();
            defaultProperties.put("time", Instant.now().toString());
            defaultProperties.put("version", "0.0.0-ERROR");
            defaultProperties.put("name", "error-build");
            defaultProperties.put("group", "error-group");
            defaultProperties.put("artifact", "error-artifact");
            return new BuildProperties(defaultProperties);
        }
    }
}

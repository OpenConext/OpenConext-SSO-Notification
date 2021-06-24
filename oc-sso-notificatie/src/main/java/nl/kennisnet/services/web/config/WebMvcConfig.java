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
package nl.kennisnet.services.web.config;

import nl.kennisnet.services.web.interceptor.DisableCacheInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main goal of this configuration is to add custom interceptor.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private DisableCacheInterceptor disableCacheInterceptor;

    public WebMvcConfig(DisableCacheInterceptor disableCacheInterceptor) {
        this.disableCacheInterceptor = disableCacheInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(disableCacheInterceptor);
    }
}

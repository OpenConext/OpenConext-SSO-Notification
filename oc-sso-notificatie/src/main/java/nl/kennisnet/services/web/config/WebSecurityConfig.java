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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This class configures the Spring Security Settings for our application.
 * <p></p>
 * Please note we added the Order(SecurityProperties.ACCESS_OVERRIDE_ORDER) to not override the actuator access rules,
 * see <a href="http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security-actuator">this link</a>.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Value("${management.security.roles:#{null}}")
    private String managementSecurityRoles;

    @Value("${security.headers.enabled}")
    private Boolean securityHeadersEnabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // This application only contains public pages. The Spring Boot Actuator Endpoints can be protected by the
        // config in Spring Boot Actuator Endpoints.
        if (null != managementSecurityRoles) {
            http.authorizeHttpRequests(authz -> authz
                    .requestMatchers("/actuator/**").hasRole(managementSecurityRoles)
                    .anyRequest().permitAll()
            ).httpBasic(Customizer.withDefaults());
        } else {
            http.authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
        }

        // We have to disable the X-Frame-Options since this SSO Notification service can be invoked within an iframe.
        // Disable all security headers so this service can be invoked within 3rd-party applications.
        if (!securityHeadersEnabled) {
            http.headers(headers -> headers
                    .frameOptions(Customizer.withDefaults())
                    .disable());
        }

        return http.build();
    }

}

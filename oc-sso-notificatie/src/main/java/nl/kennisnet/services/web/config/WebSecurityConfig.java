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

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * This class configures the Spring Security Settings for our application.
 *
 * Please note we added the Order(SecurityProperties.ACCESS_OVERRIDE_ORDER) to not override the actuator access rules,
 * see http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security-actuator.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * The properties for the security aspects of the application (containing the user to protect the management
     * information with.)
     */
    private SecurityProperties securityProperties;

    public WebSecurityConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        // Enable an in memory based authentication for Spring Actuator using the security user application properties.
        // The application property file contains the password as a BCrypt hashed value, the uid generated
        // password value for the admin user if not supplied is not supported and won't be logged either.
        // Since this value cannot be used.
        SecurityProperties.User user = securityProperties.getUser();
        List<String> roles = user.getRoles();
        builder.inMemoryAuthentication().passwordEncoder(passwordEncoder())
                .withUser(user.getName()).password(user.getPassword())
                .roles(roles.toArray(new String[roles.size()]));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // This application only contains public pages. The Spring Boot Actuator Endpoints
        // are protected by the config in Spring Boot Actuator Endpoints.
        http.authorizeRequests().anyRequest().permitAll();

        // We have to disable the X-Frame-Options since this SSO Notification service can be invoked within an iframe.
        http.headers().frameOptions().disable();

        // Disable all security headers so this service can be invoked within 3rd-party applications.
        http.headers().disable();
    }

    /**
     * A BCryptPasswordEncoder to use as passwordEncoder.
     *
     * @return a BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

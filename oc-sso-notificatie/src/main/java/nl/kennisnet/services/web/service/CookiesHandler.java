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

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import nl.kennisnet.services.web.model.CookieValueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

/**
 * Handler performs different operations on cookies
 */
@Component
public class CookiesHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookiesHandler.class);

    @Value("${notification.cookie.domain}")
    private String domain;

    @Value("${notification.cookie.secured:false}")
    private boolean secured;

    @Value("${notification.cookie.path:/}")
    private String path;

    private final CryptoManager cryptoManager;

    public CookiesHandler(CryptoManager cryptoManager) {
        this.cryptoManager = cryptoManager;
    }

    /**
     * Removes cookie with given name, if it presents.
     *
     * @param cookieName cookie name.
     * @param request    HTTP Servlet Request.
     * @param response   HTTP Servlet Response.
     */
    public void removeCookieIfPresent(String cookieName, HttpServletRequest request, HttpServletResponse response) {
        Cookie tgtCookie = WebUtils.getCookie(request, cookieName);
        if (null != tgtCookie) {
            tgtCookie.setValue(StringUtils.EMPTY);
            tgtCookie.setMaxAge(0);
            response.addCookie(tgtCookie);
        }
    }

    /**
     * Creates cookie with given name and generated based on given id and url value.
     *
     * @param name cookie name.
     * @param id   IdP identifier create cookie value to.
     * @param url  url create cookie value to.
     * @return new cookie created using given parameters for the content
     * and setting other properties using {@link Cookie#setDomain(String)},
     * {@link Cookie#setSecure(boolean)} }
     */
    public Cookie createCookie(String name, String id, URL url) throws JsonProcessingException {
        return createCookie(name, createCookieValue(id, url));
    }

    private String createCookieValue(String id, URL url) throws JsonProcessingException {
        // Create JSON mapping
        ObjectMapper objectMapper = new ObjectMapper();
        return URLEncoder.encode(cryptoManager.encrypt(
                objectMapper.writeValueAsString(new CookieValueDTO(id, url.toString()))), StandardCharsets.UTF_8);
    }

    private Cookie createCookie(String name, String value) {
        assert value != null : "Supplied value == null";

        Cookie cookie = new Cookie(name, value);
        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
            LOGGER.debug("Created domain cookie on: {}", domain);
        }

        cookie.setPath(path);
        cookie.setSecure(secured);
        cookie.setHttpOnly(true);

        LOGGER.debug("Created '{}' on path={}.", name, cookie.getPath());
        return cookie;
    }

}

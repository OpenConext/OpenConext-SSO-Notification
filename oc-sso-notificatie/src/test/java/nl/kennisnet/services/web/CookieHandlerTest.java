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
package nl.kennisnet.services.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import nl.kennisnet.services.web.controller.SsoNotificationController;
import nl.kennisnet.services.web.model.CookieValueDTO;
import nl.kennisnet.services.web.service.CookiesHandler;
import nl.kennisnet.services.web.util.CookieDecrypter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.Cookie;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CookieHandlerTest {

    @Autowired
    private CookiesHandler handler;

    @Autowired
    private CookieDecrypter cookieDecrypter;

    @Value("${notification.cookie.domain}")
    private String domain;

    @Value("${notification.cookie.secured:false}")
    private boolean secured;

    @Value("${notification.cookie.path:/}")
    private String path;

    @Value("${tgt.cookie.name}")
    private String tgtCookieName;

    private static final String testEntityId = "testEntityId";

    private static final String testUrl = "https://testUrl";

    private static final String testRealm = "testRealm";

    @Test
    void createCookieNoRealmTest() throws Exception {
        // Create Cookie
        Cookie cookie = handler.createCookie(SsoNotificationController.COOKIE_NOTIFICATION, testEntityId,
                new URL(testUrl), null);

        // Check Cookie
        assertNotNull(cookie);
        assertEquals(SsoNotificationController.COOKIE_NOTIFICATION, cookie.getName());
        assertNotEquals(0, cookie.getValue().length());
        assertEquals(domain, cookie.getDomain());
        assertEquals(path, cookie.getPath());
        assertEquals(secured, cookie.getSecure());

        // Check Cookie value
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString =
                cookieDecrypter.decrypt(URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));

        CookieValueDTO cookieValueDTO = objectMapper.readValue(jsonString, CookieValueDTO.class);
        assertEquals(testEntityId, cookieValueDTO.getEntityId());
        assertEquals(testUrl, cookieValueDTO.getUrl());
        assertNull(cookieValueDTO.getRealm());
    }

    @Test
    void createCookieRealmTest() throws Exception {
        // Create Cookie
        Cookie cookie = handler.createCookie(SsoNotificationController.COOKIE_NOTIFICATION, testEntityId,
                new URL(testUrl), testRealm);

        // Check Cookie
        assertNotNull(cookie);
        assertEquals(SsoNotificationController.COOKIE_NOTIFICATION, cookie.getName());
        assertNotEquals(0, cookie.getValue().length());
        assertEquals(domain, cookie.getDomain());
        assertEquals(path, cookie.getPath());
        assertEquals(secured, cookie.getSecure());

        // Check Cookie value
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString =
                cookieDecrypter.decrypt(URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));

        CookieValueDTO cookieValueDTO = objectMapper.readValue(jsonString, CookieValueDTO.class);
        assertEquals(testEntityId, cookieValueDTO.getEntityId());
        assertEquals(testUrl, cookieValueDTO.getUrl());
        assertEquals(testRealm, cookieValueDTO.getRealm());
    }

    @DirtiesContext
    @Test
    void createCookieNoVersionTest() throws Exception {
        // Create Cookie
        Cookie cookie = handler.createCookie(SsoNotificationController.COOKIE_NOTIFICATION, testEntityId,
                new URL(testUrl), null);

        // Check Cookie
        assertNotNull(cookie);
        assertEquals(SsoNotificationController.COOKIE_NOTIFICATION, cookie.getName());
        assertNotEquals(0, cookie.getValue().length());
        assertEquals(domain, cookie.getDomain());
        assertEquals(path, cookie.getPath());
        assertEquals(secured, cookie.getSecure());

        // Check Cookie value
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString =
                cookieDecrypter.decrypt(URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));

        CookieValueDTO cookieValueDTO = objectMapper.readValue(jsonString, CookieValueDTO.class);
        assertEquals(testEntityId, cookieValueDTO.getEntityId());
        assertEquals(testUrl, cookieValueDTO.getUrl());
        assertNull(cookieValueDTO.getRealm());
    }

    @DirtiesContext
    @Test
    void createCookieNoDomainTest() throws Exception {
        ReflectionTestUtils.setField(handler, "domain", "");

        // Create Cookie
        Cookie cookie =
                handler.createCookie(SsoNotificationController.COOKIE_NOTIFICATION, testEntityId, new URL(testUrl), null);

        // Check Cookie
        assertNotNull(cookie);
        assertEquals(SsoNotificationController.COOKIE_NOTIFICATION, cookie.getName());
        assertNotEquals(0, cookie.getValue().length());
        assertNull(cookie.getDomain());
        assertEquals(path, cookie.getPath());
        assertEquals(secured, cookie.getSecure());

        // Check Cookie value
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = cookieDecrypter.decrypt(URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));
        CookieValueDTO cookieValueDTO = objectMapper.readValue(jsonString, CookieValueDTO.class);
        assertEquals(testEntityId, cookieValueDTO.getEntityId());
        assertEquals(testUrl, cookieValueDTO.getUrl());
        assertNull(cookieValueDTO.getRealm());
    }

    @Test
    void removeCookieNotPresentTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.removeCookieIfPresent(SsoNotificationController.COOKIE_NOTIFICATION, request, response);

        assertNull(response.getCookie(SsoNotificationController.COOKIE_NOTIFICATION));
    }

    @Test
    void removeCookiePresentTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(tgtCookieName, "differentvalue"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.removeCookieIfPresent(tgtCookieName, request, response);

        Cookie cookie = response.getCookie(tgtCookieName);
        assertNotNull(cookie);
        assertEquals(StringUtils.EMPTY, cookie.getValue());
        assertEquals(0, cookie.getMaxAge());
        assertEquals(tgtCookieName, cookie.getName());
    }

}

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
package nl.kennisnet.services.web.controller;

import com.google.common.collect.Lists;
import nl.kennisnet.services.web.config.CacheConfig;
import nl.kennisnet.services.web.model.IdP;
import nl.kennisnet.services.web.service.CookiesHandler;
import nl.kennisnet.services.web.service.IdPProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SsoNotificationControllerTest {

    @Value("${tgt.cookie.name}")
    private String tgtCookieName;

    private static final String SINGLE_URL = "singleUrl";
    private static final String SINGLE_URL_WITH_PATH = "singleUrlWithPath";
    private static final String INVALID = "invalid";

    private static final String MULTIPLE_URLS = "multipleUrls";
    private static final String NO_PATH_URL_IDP = "noIdpUrlConfigured";
    private static final String NO_PATH_URL_REDIRECT = "noRedirectUrlConfigured";

    private static final String SINGLE_WILDCARD_URL = "singleWildcardUrl";

    private static final String SSO_NOTIFICATION_URL = "/";
    private static final String URL = "url";
    private static final String REDIRECT_URI = "redirectUri";
    private MockMvc mvc;

    @InjectMocks
    private SsoNotificationController controller;

    @Mock
    private CacheConfig cacheConfig;

    @Mock
    private CookiesHandler cookiesHandler;

    @Mock
    private IdPProvider idPProvider;

    private ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

    @BeforeEach
    void setUp() throws Exception {
        this.mvc = MockMvcBuilders.standaloneSetup(controller).build();

        ArrayList<IdP> ssoNotifications = new ArrayList<>();
        ssoNotifications.add(new IdP(NO_PATH_URL_IDP, null, Lists.newArrayList("http://www.example.com")));
        ssoNotifications.add(new IdP(NO_PATH_URL_REDIRECT, Lists.newArrayList("http://www.example.com"), null));
        ssoNotifications.add(new IdP(SINGLE_URL, Lists.newArrayList("http://www.example.com"),
                Lists.newArrayList("https://applicatie.nl")));

        ssoNotifications.add(new IdP(MULTIPLE_URLS,
                Lists.newArrayList("http://www.example.com", "http://www.exampledomain.com"),
                Lists.newArrayList("http://www.example.com", "http://www.exampledomain.com")));

        ssoNotifications.add(new IdP(SINGLE_WILDCARD_URL, Lists.newArrayList("http://*.example.com"), null));
        ssoNotifications.add(new IdP(SINGLE_URL_WITH_PATH, Lists.newArrayList("http://www.example.com"),
                Lists.newArrayList("http://www.example.com")));

        when(idPProvider.getAllSsoNotifications()).thenReturn(ssoNotifications);
        when(cookiesHandler.createCookie(anyString(), nullable(String.class), any(URL.class))).thenReturn(
                new Cookie(SsoNotificationController.COOKIE_NOTIFICATION, "testValue"));

        ReflectionTestUtils.setField(controller, "tgtCookieName", tgtCookieName);
    }

    @Test
    void processSsoNotificationNoIdParamTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Required String parameter 'id' is not present")));
    }

    @Test
    void processSsoNotificationNoIdPTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", INVALID)
                .param(REDIRECT_URI, "https://applicatie.nl"))
                .andExpect(status().isForbidden());
    }

    @Test
    void processSsoNotificationEmptyUrlExpTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL)
                .param(REDIRECT_URI, "https://applicatie.nl"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processSsoNotificationNoUrlParamNoReferrerHeaderTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL)
                .param(REDIRECT_URI, "https://applicatie.nl"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processSsoNotificationUnauthorizedURITest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL)
                .param(REDIRECT_URI, "https://applicatie.nl")
                .param(URL, "https://applicatie.nl"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void processSsoNotificationUnauthorizedReferrerTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL)
                .param(REDIRECT_URI, "https://applicatie.nl")
                .header(HttpHeaders.REFERER, "applicatie.nl"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void processSsoNotificationInvalidReferrerBasicAuthTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", "multipleUrls")
                .param(URL, "http://www.example.com")
                .param(REDIRECT_URI, "http://test@www.exampledomain.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void processSsoNotificationNoRedirectUrlParamTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL)
                .param(URL, "http://www.example.com"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(SsoNotificationController.COOKIE_NOTIFICATION));
    }

    @Test
    void processSsoNotificationRedirectTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", "multipleUrls")
                .param(URL, "http://www.example.com")
                .param(REDIRECT_URI, "http://www.exampledomain.com"))
                .andExpect(cookie().exists(SsoNotificationController.COOKIE_NOTIFICATION))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://www.exampledomain.com"));
    }

    @Test
    void processSsoNotificationFromIframeTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", "multipleUrls")
                .param(URL, "http://www.example.com"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(SsoNotificationController.COOKIE_NOTIFICATION));
    }

    @Test
    void processSsoNotificationFromIframeWithRedirectTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", "multipleUrls")
                .param(REDIRECT_URI, "http://www.example.com")
                .param(URL, "http://www.example.com"))
                .andExpect(cookie().exists(SsoNotificationController.COOKIE_NOTIFICATION))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://www.example.com"));
    }

    @Test
    void processSsoNotificationFromIframeWildcardTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_WILDCARD_URL)
                .param(URL, "http://test.example.com"))
                .andExpect(cookie().exists(SsoNotificationController.COOKIE_NOTIFICATION))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_WILDCARD_URL)
                .param(URL, "http://subdomein.example.com"))
                .andExpect(cookie().exists(SsoNotificationController.COOKIE_NOTIFICATION))
                .andExpect(status().isOk());
    }

    @Test
    void processSsoNotificationInvalidIdTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", "test")
                .param(URL, "http://www.example.com")
                .param(REDIRECT_URI, "www.exampledomain.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void processSsoNotificationFromIframeWithInvalidRedirectTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", "multipleUrls")
                .param(REDIRECT_URI, "http://www.notinsope.com")
                .param(URL, "http://www.example.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void processSsoNotificationNoIdpExpressionsTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", NO_PATH_URL_IDP)
                .param(REDIRECT_URI, "http://www.example.com")
                .param(URL, "http://www.example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void processSsoNotificationNoRedirectExpressionsTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", NO_PATH_URL_REDIRECT)
                .param(REDIRECT_URI, "http://www.example.com")
                .param(URL, "http://www.example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidateCookieTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", "multipleUrls")
                .param(REDIRECT_URI, "http://www.example.com")
                .param(URL, "http://www.example.com")
                .cookie(new Cookie(SsoNotificationController.COOKIE_NOTIFICATION, "differentvalue"))
                .cookie(new Cookie(tgtCookieName, "tgtvalue")))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://www.example.com"));
        // Let's make sure the tgt cookie will be removed
        verify(cookiesHandler).removeCookieIfPresent(argumentCaptor.capture(), any(HttpServletRequest.class),
                any(HttpServletResponse.class));

        assertEquals(argumentCaptor.getValue(), tgtCookieName);
    }

    @Test
    void testUrlPathTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL_WITH_PATH)
                .header(HttpHeaders.REFERER, "http://www.example.com"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(SsoNotificationController.COOKIE_NOTIFICATION));

        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL_WITH_PATH)
                .header(HttpHeaders.REFERER, "http://www.example.com")
                .param(REDIRECT_URI, "http://www.example.com"))
                .andExpect(cookie().exists(SsoNotificationController.COOKIE_NOTIFICATION))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://www.example.com"));

        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL_WITH_PATH)
                .header(HttpHeaders.REFERER, "http://www.example.com/test/with/path")
                .param(REDIRECT_URI, "http://www.example.com"))
                .andExpect(cookie().exists(SsoNotificationController.COOKIE_NOTIFICATION))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://www.example.com"));
    }

    @Test
    void processNotExistingId() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", INVALID)
                .param(URL, "http://test.example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDataFetchError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL_WITH_PATH)
                .param(REDIRECT_URI, "http://www.example.com")
                .header(HttpHeaders.REFERER, "http://www.example.com"));

        when(idPProvider.getAllSsoNotifications()).thenReturn(Lists.newArrayList());

        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL_WITH_PATH)
                .param(REDIRECT_URI, "http://www.example.com")
                .header(HttpHeaders.REFERER, "http://www.example.com"))
                .andExpect(status().isFound());

        verify(cacheConfig, times(1)).cacheEvict();
    }

    @Test
    void testNoData() throws Exception {
        when(idPProvider.getAllSsoNotifications()).thenReturn(Lists.newArrayList());
        mvc.perform(MockMvcRequestBuilders.get(SSO_NOTIFICATION_URL).param("id", SINGLE_URL_WITH_PATH)
                .param(REDIRECT_URI, "http://www.example.com")
                .header(HttpHeaders.REFERER, "http://www.example.com"))
                .andExpect(status().isInternalServerError());
    }

}

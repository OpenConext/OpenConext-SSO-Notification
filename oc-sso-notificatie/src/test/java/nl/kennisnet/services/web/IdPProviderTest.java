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

import nl.kennisnet.services.web.model.IdP;
import nl.kennisnet.services.web.service.IdPProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdPProviderTest {

    @Mock
    private RestClient restClient;

    @Value("${api.endpoint.url}")
    private String endpointUrl;

    private IdPProvider idPProvider;

    private Resource restResource;

    private Resource staticResource;

    @BeforeEach
    void setUp() {
        idPProvider = new IdPProvider(restClient);

        staticResource = new ClassPathResource("idp.data.json");
        restResource = new ClassPathResource("rest_response.json");

        ReflectionTestUtils.setField(idPProvider, "dataSource", restResource);
    }

    @Test
    void getIdPTest() {
        List<IdP> idps = idPProvider.getAllSsoNotifications();
        IdP idp = idps.getFirst();
        assertNotNull(idp);
        assertEquals("RefELOSAML-OpenConext", idp.getEntityId());
        assertNotNull(idp.getIdpUrlList());
        assertNotNull(idp.getRedirectUrlList());
        assertEquals(3,  idp.getIdpUrlList().size());
        assertEquals(4,  idp.getRedirectUrlList().size());
        assertTrue(idp.getIdpUrlList().contains("https://engine.vm.openconext.org"));
        assertTrue(idp.getIdpUrlList().contains("referentie.vm.openconext.org"));
        assertTrue(idp.getIdpUrlList().contains("https://referentie.vm.openconext.org"));
        assertTrue(idp.getRedirectUrlList().contains("https://engine.vm.openconext.org"));
        assertTrue(idp.getRedirectUrlList().contains("https://referentie.vm.openconext.org"));
        assertTrue(idp.getRedirectUrlList().contains("https://referentie.vm2.openconext.org"));
        assertTrue(idp.getRedirectUrlList().contains("https://referentie.vm3.openconext.org"));
    }

    @Test
    void invalidApiKeyTest() {
        ReflectionTestUtils.setField(idPProvider, "endpointUrl", "http://localhost:3000/api/sso-notification");
        ReflectionTestUtils.setField(idPProvider, "apiKeyHeaderKey", "api-key");
        ReflectionTestUtils.setField(idPProvider, "apiKeyHeaderValue", "TESTTOKEN");
        ReflectionTestUtils.setField(idPProvider, "endpointAllSuffix", "/all");

        when(restClient.get()).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        assertThrows(HttpClientErrorException.class, () -> idPProvider.getAllSsoNotifications());
    }

    @Test
    void notFoundResponseTest() {
        ReflectionTestUtils.setField(idPProvider, "endpointUrl", "http://localhost:3000/api/sso-notification");
        ReflectionTestUtils.setField(idPProvider, "apiKeyHeaderKey", "api-key");
        ReflectionTestUtils.setField(idPProvider, "apiKeyHeaderValue", "TESTTOKEN");
        ReflectionTestUtils.setField(idPProvider, "endpointAllSuffix", "/all");

        when(restClient.get()).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertTrue(idPProvider.getAllSsoNotifications().isEmpty());
    }

    @Test
    void getIdPStaticFileTest() {
        ReflectionTestUtils.setField(idPProvider, "dataSource", staticResource);

        List<IdP> idps = idPProvider.getAllSsoNotifications();
        IdP idp = idps.getFirst();
        assertNotNull(idp);
        assertEquals("xxx", idp.getEntityId());
        assertNotNull(idp.getIdpUrlList());
        assertNotNull(idp.getRedirectUrlList());
        assertEquals(2, idp.getIdpUrlList().size());
        assertEquals(1, idp.getRedirectUrlList().size());
        assertTrue(idp.getIdpUrlList().contains("https://*.vm.openconext.org"));
        assertTrue(idp.getIdpUrlList().contains("https://test.vm.openconext.org"));
        assertTrue(idp.getRedirectUrlList().contains("https://*.vm.openconext.org"));
    }

    @Test
    void getIdpInvalidResourceStaticFileTest() {
        ReflectionTestUtils.setField(idPProvider, "dataSource", new ClassPathResource("file_does_not_exist"));
        List<IdP> idps = idPProvider.getAllSsoNotifications();
        assertTrue(idps.isEmpty());
    }

}

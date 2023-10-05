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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(IdPProvider.class)
@TestPropertySource(properties = "classpath:application.properties")
class IdPProviderTest {

    @Autowired
    private IdPProvider provider;

    @Autowired
    private MockRestServiceServer server;

    @Value("classpath:rest_response.json")
    private Resource resourceFile;

    @Value("${api.endpoint.url}")
    private String endpointUrl;

    @Test
    void getIdPTest() throws Exception {
        String resource = new String(Files.readAllBytes(resourceFile.getFile().toPath()));
        this.server.expect(requestTo(createUrl())).andRespond(withSuccess(resource, MediaType.APPLICATION_JSON));

        List<IdP> idps = provider.getAllSsoNotifications();
        IdP idp = idps.get(0);
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
        this.server.verify();
    }

    @Test
    void invalidApiKeyTest() {
        this.server.expect(requestTo(createUrl())).andRespond(withUnauthorizedRequest());
        assertThrows(HttpClientErrorException.Unauthorized.class, () -> provider.getAllSsoNotifications());
    }

    @Test
    void notFoundResponseTest()  {
        this.server.expect(requestTo(createUrl())).andRespond(withStatus(HttpStatus.NOT_FOUND));
        assertTrue(provider.getAllSsoNotifications().isEmpty());
    }

    @Test
    void getIdPStaticFileTest() {
        ReflectionTestUtils.setField(provider, "endpointUrl", null);

        List<IdP> idps = provider.getAllSsoNotifications();
        IdP idp = idps.get(0);
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
        ReflectionTestUtils.setField(provider, "dataSource", new ClassPathResource("file_does_not_exist"));
        List<IdP> idps = provider.getAllSsoNotifications();
        assertTrue(idps.isEmpty());
    }

    private URI createUrl() {
        String ALL_SUFFIX = "/all";
        return UriComponentsBuilder.fromUriString(endpointUrl + ALL_SUFFIX).build().toUri();
    }

}

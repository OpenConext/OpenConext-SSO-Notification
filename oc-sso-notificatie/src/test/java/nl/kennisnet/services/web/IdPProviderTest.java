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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@RestClientTest(IdPProvider.class)
@TestPropertySource(properties = "classpath:application.properties")
class IdPProviderTest {

    @Autowired
    private IdPProvider provider;

    @Test
    void getIdPTest() {
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
    void getIdpInvalidResourceTest() {
        ReflectionTestUtils.setField(provider, "dataSource", new ClassPathResource("file_does_not_exist"));
        List<IdP> idps = provider.getAllSsoNotifications();
        assertTrue(idps.isEmpty());
    }

}

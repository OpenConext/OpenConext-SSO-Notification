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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class UrlMatchWildCardsTest {

    @Test
    void matchFirstSubDomainWildcardsTest() {
        Assertions.assertTrue(SsoNotificationController.matchWildcards("http://subdomein.exampledomain.com",
                "http://*.exampledomain.com"));
    }

    @Test
    void matchFirstSubDomainWildcardsIgnoreProtocolTest() {
        Assertions.assertTrue(SsoNotificationController.matchWildcards("https://subdomein.exampledomain.com",
                "http://*.exampledomain.com"));
    }

    @Test
    void matchFirstSubDomainWithTrailingTest() {
        Assertions.assertTrue(SsoNotificationController.matchWildcards(
                "http://subdomain.exampledomain.com/some-page/some-template.html", "http://*.exampledomain.com"));
    }

    @Test
    void matchSecondSubDomainWildcardsTest() {
        Assertions.assertTrue(SsoNotificationController.matchWildcards("http://subsub.subdomain.exampledomain.com",
                "http://*.subdomain.exampledomain.com"));
    }

    @Test
    void matchSecondSubDomainWildcardsFailTest() {
        Assertions.assertFalse(SsoNotificationController.matchWildcards("http://subdomein.exampledomain.com",
                "http://*.subdomein.exampledomain.com"));
    }

    @Test
    void matchLastSubDomainWildcardsTest1() {
        Assertions.assertTrue(SsoNotificationController.matchWildcards("http://subdomein.exampledomain.com/",
                "http://*.exampledomain.com"));
    }

    @Test
    void matchLastSubDomainWildcardsTest2() {
        Assertions.assertTrue(SsoNotificationController.matchWildcards("http://subdomein.exampledomain.com",
                "http://*.exampledomain.com/"));
    }

    @Test
    void exactMatchTest1() {
        Assertions.assertTrue(SsoNotificationController.matchWildcards("http://subdomein.exampledomain.com",
                "http://subdomein.exampledomain.com"));
    }

    @Test
    void exactMatchTest2() {
        Assertions.assertTrue(SsoNotificationController.matchWildcards("http://subdomein.exampledomain.com",
                "http://subdomein.exampledomain.com/"));
    }

    @Test
    void exactMatchTest3() {
        Assertions.assertTrue(SsoNotificationController.matchWildcards("https://subdomein.exampledomain.com",
                "http://subdomein.exampledomain.com/"));
    }

    @Test
    void notMatchSubDomainWildcardsTest() {
        Assertions.assertFalse(SsoNotificationController.matchWildcards(
                "http://subdomein.exampledomain.com.hackernet.nl", "http://*.exampledomain.com"));
    }
}

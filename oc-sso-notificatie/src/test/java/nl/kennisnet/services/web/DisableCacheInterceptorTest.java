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

import nl.kennisnet.services.web.interceptor.DisableCacheInterceptor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.support.WebContentGenerator;

import static org.hamcrest.MatcherAssert.assertThat;

class DisableCacheInterceptorTest {

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setMethod(WebContentGenerator.METHOD_GET);
        response = new MockHttpServletResponse();
    }

    @Test
    void testExpireHeader() throws Exception {
        HandlerInterceptor interceptor = new DisableCacheInterceptor();
        interceptor.preHandle(request, response, null);
        Iterable<String> headers = response.getHeaders(HttpHeaders.EXPIRES);
        assertThat(headers, Matchers.hasItem("-1"));
    }

    @Test
    void testCacheHttp10Protocol() throws Exception {
        HandlerInterceptor interceptor = new DisableCacheInterceptor();
        request.setProtocol("HTTP/1.0");
        interceptor.preHandle(request, response, null);
        Iterable<String> headers = response.getHeaders(HttpHeaders.PRAGMA);
        assertThat(headers, Matchers.hasItem("no-cache"));
    }

    @Test
    void testCacheHttp11Protocol() throws Exception {
        HandlerInterceptor interceptor = new DisableCacheInterceptor();
        request.setProtocol("HTTP/1.1");
        interceptor.preHandle(request, response, null);
        Iterable<String> headers = response.getHeaders(HttpHeaders.CACHE_CONTROL);
        assertThat(headers, Matchers.hasItem("no-store, no-cache, must-revalidate"));
    }
}

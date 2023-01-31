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
package nl.kennisnet.services.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor to disable response caching.
 * <p></p>
 * Based on the protocol different headers are set:
 * <p></p>
 * For HTTP/1.0 the no-cache value is set for the Pragma header.
 * For HTTP/1.1 the value no-store, no-cache, must-revalidate is set for the Cache-Control header.
 * <p></p>
 * The Expires header with -1 is always added.
 */
@Component
public class DisableCacheInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {
        if("HTTP/1.0".equalsIgnoreCase(request.getProtocol())) {
            response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        } else if("HTTP/1.1".equalsIgnoreCase(request.getProtocol())) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate");
        }

        response.setHeader(HttpHeaders.EXPIRES, "-1");
        return true;
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o,
                           ModelAndView modelAndView)  {
        // This implementation is empty. This functionality is not required to set specific headers.
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) {

        // This implementation is empty. This functionality is not required to set specific headers.
    }
}

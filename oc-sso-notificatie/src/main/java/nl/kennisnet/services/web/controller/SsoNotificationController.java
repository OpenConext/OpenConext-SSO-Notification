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

import nl.kennisnet.services.web.config.CacheConfig;
import nl.kennisnet.services.web.model.IdP;
import nl.kennisnet.services.web.service.CookiesHandler;
import nl.kennisnet.services.web.service.IdPProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This controller represents the SSO Notification service.
 */
@Controller
public class SsoNotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SsoNotificationController.class);

    private static final Logger EVENT_LOGGER = LoggerFactory.getLogger("oc-sso-eventlogger");

    // Exceptions messages
    private static final String EXCEPTION_DATA_COULD_NOT_BE_RETRIEVED =
            "SSO Notifications data could not be retrieved.";

    private static final String EXCEPTION_UNKNOWN_ID = "Unknown 'id' parameter found in request: ";

    private static final String EXCEPTION_ID_NOT_PROVIDED = "Required String parameter 'id' is not present";

    private static final String EXCEPTION_NO_VALID_URL_EXPRESSION_IDP =
            "No valid URL expression associated with the IdP available for IdP with ID: ";

    private static final String EXCEPTION_NO_VALID_URL_EXPRESSION_REDIRECT =
            "No valid URL expression associated with the Redirect available for IdP with ID: ";

    private static final String EXCEPTION_NO_REFERER_OR_URL = "No referer header or url in request";

    private static final String EXCEPTION_URL_CONTAINS_BASIC_AUTH =
            "The supplied URL is not allowed to contain basic auth parameters";

    private static final String EXCEPTION_URL_NOT_AUTHORIZED =
            "The configured url is not authorized to make this request";

    // Log messages
    private static final String EVENT = "event";

    private static final String IDP = "idpId";

    private static final String SSONOT_FAILED = "SSONOT_FAILED";

    private static final String SSONOT_REDIRECT = "SSONOT_REDIRECT";

    private static final String SSONOT_SUCCESS= "SSONOT_SUCCESS";

    /** The name of the SSO Cookie notification ({@value}) */
    public static final String COOKIE_NOTIFICATION = "ssonot";

    /** The name of the TGT cookie */
    @Value("${tgt.cookie.name}")
    private String tgtCookieName;

    private final CacheConfig cacheConfig;

    private final CookiesHandler cookiesHandler;

    private final IdPProvider idPProvider;

    private List<IdP> ssoNotifications = new ArrayList<>();

    private static final Pattern WILDCARD_DOMAIN = Pattern.compile("(?<=\\*)(.*?)(?=\\/|$)");

    private static final Pattern REDIRECT_DOMAIN = Pattern.compile("(.*?)(?=\\/|$)");

    public SsoNotificationController(CacheConfig cacheConfig, CookiesHandler cookiesHandler, IdPProvider idPProvider) {
        this.cacheConfig = cacheConfig;
        this.cookiesHandler = cookiesHandler;
        this.idPProvider = idPProvider;
    }

    /**
     * Processes the SSO Notification request.
     *
     * @param id the identifier of the requested IdP (using id as parameter).
     * @param url the ELO callback elo to use for the elo
     * @param redirectUri url redirect to.
     * @param referrer creating cookie for.
     * @param request HTTP Servlet Request.
     * @param response HTTP Servlet Response.
     *
     * @throws ResponseStatusException If redirect is invalid.
     */
    @GetMapping(value = "/")
    public void processSsoNotification(@RequestParam(required = false) String id,
                                       @RequestParam(required = false) String url,
                                       @RequestParam(required = false) String redirectUri,
                                       @RequestHeader(value = HttpHeaders.REFERER, required = false) String referrer,
                                       @CookieValue(value = COOKIE_NOTIFICATION, required = false) String notificationCookie,
                                       HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        LOGGER.info("Request received with id ('{}') url ('{}') redirectUri ('{}') referrer ('{}') " +
                        "notificationCookie ('{}')", id, url, redirectUri, referrer, notificationCookie);

        // Add IdP id to logback and set default to failed
        MDC.put(IDP, String.valueOf(id));
        MDC.put(EVENT, SSONOT_FAILED);

        List<IdP> remoteSsoNotifications = idPProvider.getAllSsoNotifications();

        // If the remote SSO Notifications data is empty, something probably went wrong. Use the "old" cache and
        // discard the new remote cache.
        if (remoteSsoNotifications.isEmpty()) {
            remoteSsoNotifications = ssoNotifications;
            cacheConfig.cacheEvict();
        }
        ssoNotifications = remoteSsoNotifications;

        if (ssoNotifications.isEmpty()) {
            EVENT_LOGGER.error("SSO Notifications data could not be retrieved. Please check if the Data Services " +
                    "application is still running.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, EXCEPTION_DATA_COULD_NOT_BE_RETRIEVED);
        }

        IdP idp = ssoNotifications.stream().filter(p -> p.getEntityId().equalsIgnoreCase(id)).findAny().orElse(null);
        verifyIdP(idp, id, redirectUri);
        URL createdUrl = determineAndVerifyURL(idp, url, referrer);

        // Set notification cookie
        LOGGER.info("Setting notification Cookie ('{}') for id ('{}') with url ('{}')",
                COOKIE_NOTIFICATION, id, createdUrl);

        Cookie cNotification = cookiesHandler.createCookie(COOKIE_NOTIFICATION, id, createdUrl);
        response.addCookie(cNotification);
        response.addHeader("Content-Type", "application/javascript");

        // Remove TGT cookie if notification cookie changed
        if (!cNotification.getValue().equals(notificationCookie)) {
            LOGGER.info("Removing TGT Cookie if present ('{}').", tgtCookieName);
            cookiesHandler.removeCookieIfPresent(tgtCookieName, request, response);
        }

        if (redirectUri != null) {
            MDC.put(EVENT, SSONOT_REDIRECT);
            EVENT_LOGGER.info("Redirecting client back to portal at: ('{}')", redirectUri);
            response.sendRedirect(redirectUri);
            return;
        }
        MDC.put(EVENT, SSONOT_SUCCESS);
        EVENT_LOGGER.info("Finished without redirecting");
    }

    /**
     * Verify if the supplied url is allowed (whitelisted) and a valid format.
     *
     * @param configuredUrlReferrers the urls to verify against. (including https://*.example.com)
     * @param url the url to verify
     * @param context The identifier of the parameter which is used to retrieve the supplied url.
     * @return the supplied url converted into a URL if the url is whitelisted formatted well.
     *
     * @throws ResponseStatusException if the supplied url is not a valid url.
     */
    private URL verifyUrl(List<String> configuredUrlReferrers, String url, String context) {

        URL result;
        try {
            result = new URL(url);
        } catch (MalformedURLException e) {
            EVENT_LOGGER.warn("The supplied url ('{}') is not a valid {}", url, context);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The supplied URL is not a valid " + context);
        }

        // ENT-3232 prevent misuse of basic authentication parameters
        if (url.contains("@")){
            EVENT_LOGGER.warn("The URL ('{}') is not allowed to contain basic auth parameters", url);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, EXCEPTION_URL_CONTAINS_BASIC_AUTH);
        }

        if (configuredUrlReferrers.stream().noneMatch(urlReferrer -> matchWildcards(result.toString(), urlReferrer))) {
            EVENT_LOGGER.warn("The URL ('{}') does not match URL expression(s): ('{}') which is used in the context of {}",
                    result, configuredUrlReferrers, context);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, EXCEPTION_URL_NOT_AUTHORIZED);
        }

        return result;
    }

    /**
     * Verify the retrieved IdP is valid.
     * <p></p>
     * The idp is only valid if the idp exists and if urls are configured to white list.
     *
     * @param idp the retrieved idp
     * @param id the id used to retrieve the idp
     * @param redirectUri the redirect URL used (OPTIONAL)
     *
     * @throws ResponseStatusException if the idp is invalid.
     */
    private void verifyIdP(IdP idp, String id, String redirectUri) {
        if (null == id) {
            EVENT_LOGGER.warn(EXCEPTION_ID_NOT_PROVIDED);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, EXCEPTION_ID_NOT_PROVIDED);
        }

        if (null == idp) {
            EVENT_LOGGER.warn("Unknown 'id' parameter found in request: {}", id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, EXCEPTION_UNKNOWN_ID + id);
        }

        if (CollectionUtils.isEmpty(idp.getIdpUrlList())) {
            EVENT_LOGGER.warn("No valid URL expression associated with the IdP available for IdP with ID: {}", id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, EXCEPTION_NO_VALID_URL_EXPRESSION_IDP + id);
        }

        if (redirectUri != null) {
            if (CollectionUtils.isEmpty(idp.getRedirectUrlList())) {
                EVENT_LOGGER.warn("No valid URL expression associated with the Redirect available for IdP with ID: {}", id);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, EXCEPTION_NO_VALID_URL_EXPRESSION_REDIRECT + id);
            }
            verifyUrl(idp.getRedirectUrlList(), redirectUri, "redirectUri");
        }

        LOGGER.debug("Idp found with id ('{}') and idpUrls ('{}')", idp.getEntityId(), idp.getIdpUrlList());
    }

    /**
     * Verify the supplied url matches the pattern. This pattern is limited to *.
     *
     * @param url the url to verify
     * @param pattern the pattern which might contain http://*.example.com
     * @return true if the url matches the pattern.
     */
    static boolean matchWildcards(String url, String pattern) {
        Matcher urlMatch = REDIRECT_DOMAIN.matcher(url.replaceFirst("(https?:\\/\\/)", ""));
        boolean match = false;

        if (pattern.contains("*.")) {
            Matcher patternMatch = WILDCARD_DOMAIN.matcher(pattern);

            if (patternMatch.find() && urlMatch.find()) {
                match = urlMatch.group().endsWith(patternMatch.group());
            }
        } else {
            Matcher patternMatch = REDIRECT_DOMAIN.matcher(pattern.replaceFirst("(https?:\\/\\/)", ""));

            if (patternMatch.find() && urlMatch.find()) {
                match = urlMatch.group().equals(patternMatch.group());
            }
        }
        return match;
    }

    /**
     * Determine the url we should use to set as url in the SSO notification cookie.
     *
     * @param idp the idp
     * @param url the supplied url if any.
     * @param referrer the referrer if any
     * @return the url to use or <code>null</code> if no url should be used.
     *
     * @throws ResponseStatusException if the supplied input is not valid.
     */
    private URL determineAndVerifyURL(IdP idp, String url, String referrer) {
        URL result;
        if (null != url) {
            // The url takes precedence if supplied.
            result = verifyUrl(idp.getIdpUrlList(), url, "url");
        } else {
            // We have to use the referrer to construct the url.
            if (null == referrer) {
                EVENT_LOGGER.warn(EXCEPTION_NO_REFERER_OR_URL);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, EXCEPTION_NO_REFERER_OR_URL);
            }

            // For security reasons we verify the referrer as well.
            LOGGER.info("Verifying Referer (referrer: '{}', urlExp: '{}')", referrer, idp.getIdpUrlList());
            result = verifyUrl(idp.getIdpUrlList(), referrer, "referrer");
        }
        return result;
    }
}

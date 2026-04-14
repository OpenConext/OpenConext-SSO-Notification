package nl.kennisnet.services.web;

import nl.kennisnet.services.web.service.CacheHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheHashServiceTest {

    @Mock
    RestClient restClient;

    private CacheHashService cacheHashService;

    @BeforeEach
    void setUp() {
        cacheHashService = new CacheHashService(restClient);

        ReflectionTestUtils.setField(cacheHashService, "url", "sso-notification-url");
        ReflectionTestUtils.setField(cacheHashService, "API_KEY_HEADER", "api-key");
    }

    @Test
    void fetchCacheHashTest() {
        RestClient.RequestHeadersUriSpec<?> spec = buildRequestSpec("HASH");

        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) spec);

        assertEquals("HASH", cacheHashService.fetchCacheHash());
    }

    @Test
    void fetchCacheNullReturnTest() {
        RestClient.RequestHeadersUriSpec<?> spec = buildRequestSpec(null);

        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) spec);

        assertEquals("", cacheHashService.fetchCacheHash());
    }

    @Test
    void fetchCacheNullUrlTest() {
        ReflectionTestUtils.setField(cacheHashService, "url", null);

        assertEquals("", cacheHashService.fetchCacheHash());
    }

    @Test
    void fetchCacheHashHttpExceptionTest() {
        when(restClient.get()).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        assertEquals("", cacheHashService.fetchCacheHash());
    }

    @Test
    void fetchCacheRestClientExceptionTest() {
        when(restClient.get()).thenThrow(new RestClientException("ERROR"));

        assertEquals("", cacheHashService.fetchCacheHash());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private RestClient.RequestHeadersUriSpec<?> buildRequestSpec(String responseBody) {

        RestClient.RequestHeadersUriSpec requestSpec =
                mock(RestClient.RequestHeadersUriSpec.class);

        RestClient.RequestHeadersSpec headersSpec =
                mock(RestClient.RequestHeadersSpec.class);

        RestClient.ResponseSpec responseSpec =
                mock(RestClient.ResponseSpec.class);

        when(requestSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.headers(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(responseBody);

        return requestSpec;
    }

}
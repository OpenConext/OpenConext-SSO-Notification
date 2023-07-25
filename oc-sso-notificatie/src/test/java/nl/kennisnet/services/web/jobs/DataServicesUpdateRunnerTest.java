package nl.kennisnet.services.web.jobs;

import nl.kennisnet.services.web.config.CacheConfig;
import nl.kennisnet.services.web.service.CacheHashService;
import nl.kennisnet.services.web.service.jobs.DataServicesUpdateRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@SpringBootTest
public class DataServicesUpdateRunnerTest {

    @Mock
    CacheHashService cacheHashService;

    @Mock
    CacheConfig cacheConfig;

    @InjectMocks
    DataServicesUpdateRunner dataServicesUpdateRunner;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(dataServicesUpdateRunner, "lastDataServicesCacheHash", "HASH");
    }

    @Test
    void runTest() {
        ReflectionTestUtils.setField(dataServicesUpdateRunner, "running", false);

        when(cacheHashService.fetchCacheHash()).thenReturn("NEW_HASH");

        dataServicesUpdateRunner.run();

        verify(cacheConfig, times(1)).cacheEvict();
    }

    @Test
    void runSameHashTest() {
        ReflectionTestUtils.setField(dataServicesUpdateRunner, "running", false);

        when(cacheHashService.fetchCacheHash()).thenReturn("HASH");

        dataServicesUpdateRunner.run();

        verify(cacheConfig, times(0)).cacheEvict();
    }

    @Test
    void runEmptyHashTest() {
        ReflectionTestUtils.setField(dataServicesUpdateRunner, "running", false);

        when(cacheHashService.fetchCacheHash()).thenReturn("");

        dataServicesUpdateRunner.run();

        verify(cacheConfig, times(0)).cacheEvict();
    }

    @Test
    void canNotRunDouble() {
        ReflectionTestUtils.setField(dataServicesUpdateRunner, "running", true);

        dataServicesUpdateRunner.run();

        verify(cacheConfig, times(0)).cacheEvict();
    }

}
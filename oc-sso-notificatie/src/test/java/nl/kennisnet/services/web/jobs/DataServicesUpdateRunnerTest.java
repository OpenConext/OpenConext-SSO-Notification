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
package nl.kennisnet.services.web.jobs;

import nl.kennisnet.services.web.config.CacheConfig;
import nl.kennisnet.services.web.service.CacheHashService;
import nl.kennisnet.services.web.service.jobs.DataServicesUpdateRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataServicesUpdateRunnerTest {

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

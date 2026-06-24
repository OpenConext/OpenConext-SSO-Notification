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
package nl.kennisnet.services.web.actuator;

import nl.kennisnet.services.actuator.ReleaseDaysInfoContributor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.info.BuildProperties;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseDaysInfoContributorTest {

    @Mock
    private BuildProperties buildProperties;

    @InjectMocks
    private ReleaseDaysInfoContributor infoIndicator;

    @Test
    void testSeveralDays() {
        when(buildProperties.getTime()).thenReturn(Instant.now().minus(Duration.ofDays(3)));
        Info.Builder builder = new Info.Builder();
        infoIndicator.contribute(builder);
        Info info = builder.build();

        assertEquals(3L, info.get("days_since_release"));
    }

    @Test
    void testZeroDays() {
        when(buildProperties.getTime()).thenReturn(Instant.now());
        Info.Builder builder = new Info.Builder();
        infoIndicator.contribute(builder);
        Info info = builder.build();

        assertEquals(0L, info.get("days_since_release"));
    }

    @Test
    void testNoBuildInfo() {
        Info.Builder builder = new Info.Builder();
        infoIndicator.contribute(builder);
        Info info = builder.build();

        assertEquals(0L, info.get("days_since_release"));
    }

}

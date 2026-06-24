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
package nl.kennisnet.services.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Component
public class ReleaseDaysInfoContributor implements InfoContributor {

    private static final String RELEASE_DAYS_KEY = "days_since_release";

    private final BuildProperties buildProperties;

    public ReleaseDaysInfoContributor(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Override
    public void contribute(Info.Builder builder) {
        long currentDays = 0L;

        if (null != buildProperties.getTime()) {
            LocalDate releaseDate = LocalDate.ofInstant(buildProperties.getTime(), ZoneId.systemDefault());
            LocalDate currentDate = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());
            currentDays = ChronoUnit.DAYS.between(releaseDate, currentDate);
        }
        builder.withDetail(RELEASE_DAYS_KEY, currentDays);
    }

}

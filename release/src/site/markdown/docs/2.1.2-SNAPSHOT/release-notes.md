# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 2.1.2-SNAPSHOT              |
| Changes since version     | 2.1.1                       |
| Release date              | dd-mm-yyyy (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes
<!-- Please note only the stories should be added. -->

| #        | Description                         |
|:---------|:------------------------------------|
| ENT-4722 | Implement cache clear functionality |


## Configuration changes

The newly exposed cache-hash endpoint should be added to the configs to fetch the cache-hash:

    api.endpoint.url.cacheHash=http://localhost:3000/api/cache-hash/sso-notification

Furthermore, a cron schedule to retrieve the cache hash once a minute should also be added:

    dataservices.fetchCacheHash.cronSchedule=0 * * * * *

## Known vulnerabilities

This version of the application consists of the following known vulnerabilities with a CVE of 8 or higher:

### snakeyaml-1.33.jar: CVE-2022-1471 (9.8)

This dependency is used by the latest version of Spring Boot (3.0.7) and yet unresolved. This vulnerability is a false
positive (see: https://github.com/spring-projects/spring-boot/issues/33457) and added to the ignore list.

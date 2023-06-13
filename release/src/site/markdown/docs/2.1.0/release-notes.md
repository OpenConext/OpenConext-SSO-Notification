# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 2.1.0-SNAPSHOT              |
| Changes since version     | 2.0.6                       |
| Release date              | 13-06-2023 (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

| #        | Description                                             |
|:---------|:--------------------------------------------------------|
| ENT-4589 | Update OC SSO notification service to Spring Boot 3.0.7 |
| ENT-4622 | Build release OC SSO notification service 2.1.0         |

## Configuration changes

The following configuration is no longer used and can be removed.

    # The version to set for the notification cookie
    notification.cookie.version=-1

## Known vulnerabilities

This version of the application consists of the following known vulnerabilities with a CVE of 7 or higher:

### snakeyaml-1.33.jar: CVE-2022-1471 (9.8)

This dependency is used by the latest version of Spring Boot (3.0.7) and yet unresolved. This vulnerability is a false
positive (see: https://github.com/spring-projects/spring-boot/issues/33457) and added to the ignore list.

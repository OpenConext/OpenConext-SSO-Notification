# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 2.1.0-SNAPSHOT              |
| Changes since version     | 2.0.6                       |
| Release date              | dd-mm-yyyy (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

| #        | Description                                             |
|:---------|:--------------------------------------------------------|
| ENT-4589 | Update OC SSO notification service to Spring Boot 3.0.7 |

## Configuration changes

The following configuration is no longer used and can be removed.

    # The version to set for the notification cookie
    notification.cookie.version=-1

## Known vulnerabilities

This version of the application consists of the following known vulnerabilities:

### spring-web-5.3.26.jar: CVE-2016-1000027 (9.8)

This dependency is used by the latest version of Spring Boot (2.7.10) and yet unresolved. This vulnerability is a false
positive (see: https://github.com/spring-projects/spring-framework/issues/24434#issuecomment-744519525) and added to
the ignore list.

### snakeyaml-1.33.jar: CVE-2022-1471 (9.8)

This dependency is used by the latest version of Spring Boot (2.7.10) and yet unresolved. This vulnerability is a false
positive (see: https://github.com/spring-projects/spring-boot/issues/33457) and added to the ignore list.

### spring-security-config-5.7.7.jar/spring-security-web-5.7.7.jar: CVE-2023-20862(8.8)

This dependency is used by the latest version of Spring Boot (2.7.10) and yet unresolved. This application is not 
vulnerable since it does not support logout 
(see: https://securityonline.info/cve-2023-20862-high-severity-security-vulnerability-affecting-spring-framework/). 
Added to the ignore list.

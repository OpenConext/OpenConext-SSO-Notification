# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 2.0.6-SNAPSHOT              |
| Changes since version     | 2.0.5                       |
| Release date              | dd-mm-yyyy (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes
<!-- Please note only the stories should be added. -->

| #        | Description                                          |
|:---------|:-----------------------------------------------------|
| ENT-4545 | Wildcards for sub-sub domains for OC SSO-Notificatie |


## Configuration changes

## Known vulnerabilities

This version of the application consists of the following known vulnerabilities:

### spring-web-5.3.24.jar: CVE-2016-1000027 (9.8)

This dependency is used by the latest version of Spring Boot (2.7.6) and yet unresolved. This vulnerability is a false
positive (see: https://github.com/spring-projects/spring-framework/issues/24434#issuecomment-744519525) and added to
the ignore list.

### snakeyaml-1.33.jar: CVE-2022-1471 (9.8)

This dependency is used by the latest version of Spring Boot (2.7.6) and yet unresolved. This vulnerability is a false
positive (see: https://github.com/spring-projects/spring-boot/issues/33457) and added to the ignore list.

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

| #        | Description                                                   |
|:---------|:--------------------------------------------------------------|
| ENT-XXXX |                                                               |


## Configuration changes

## Known vulnerabilities

This version of the application consists of the following known vulnerabilities with a CVE of 8 or higher:

### snakeyaml-1.33.jar: CVE-2022-1471 (9.8)

This dependency is used by the latest version of Spring Boot (3.0.7) and yet unresolved. This vulnerability is a false
positive (see: https://github.com/spring-projects/spring-boot/issues/33457) and added to the ignore list.

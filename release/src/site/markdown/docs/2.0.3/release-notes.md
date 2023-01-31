# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 2.0.3                       |
| Changes since version     | 2.0.2                       |
| Release date              | 17-10-2022 (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

| #        | Description                             |
|:---------|:----------------------------------------|
| ENT-4420 | Build release OC SSO notification 2.0.3 |

## Configuration changes

## Known vulnerabilities

This version of the application consists of the following known vulnerabilities:

### jackson-databind-2.13.4.jar: CVE-2022-42003(7.5)

This dependency is used by the latest versions of Spring Boot (2.7.4) and the OWASP Dependency Checker (7.2.1). At this 
moment, a newer version of this dependency is not available. The CVE is lower than 8.0 and therefore acceptable. The 
vulnerability is added to the ignore list until the end of 2022.

### snakeyaml-1.30.jar: CVE-2022-38752(6.5), CVE-2022-38751(6.5), CVE-2022-38750(5.5), CVE-2022-25857(7.5), CVE-2022-38749(6.5)

This dependency is used by the latest version of Spring Boot (2.7.4). Nog geen nieuwere versie beschikbaar. At this
moment, a newer version of this dependency is not available. The CVE is lower than 8.0 and therefore acceptable. The
vulnerability is added to the ignore list until the end of 2022.

### spring-security-crypto-5.7.3.jar: CVE-2020-5408(6.5)

This dependency is used by the latest version of Spring Boot (2.7.4). Nog geen nieuwere versie beschikbaar. At this
moment, a newer version of this dependency is not available. The CVE is lower than 8.0 and therefore acceptable. The
vulnerability is added to the ignore list until the end of 2022.

### spring-web-5.3.23.jar: CVE-2016-1000027 (9.8)

This dependency is used by the latest version of Spring Boot (2.7.4) and yet unresolved. This vulnerability is a false 
positive (see: https://github.com/spring-projects/spring-framework/issues/24434#issuecomment-744519525) and added to 
the ignore list.

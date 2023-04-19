# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 2.0.6                       |
| Changes since version     | 2.0.5                       |
| Release date              | 19-04-2023 (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

| #        | Description                                          |
|:---------|:-----------------------------------------------------|
| ENT-4545 | Wildcards for sub-sub domains for OC SSO-Notificatie |
| ENT-4552 | Build release OC SSO notification 2.0.6              |
| ENT-4637 | Implement confederational SSO notification           |

## Configuration changes

A feature has been introduced to redirect a SSO Notification request to a secondary endpoint if an IDP was not found. 
This feature can be helpful during migrations, in which an Identity Provider / ELO may want to invoke a SSO 
Notification, but not all Identity Providers are yet available on the new federation. In this case, the SSO Notification
application will redirect the user to the old (configured) SSO Notification service instead.

To enable this feature, the following configuration should be added:

    passthru.endpoint=https://vm.openconext.org/oc-sso-notification/?id={0}&url={1}&redirectUri={2}


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

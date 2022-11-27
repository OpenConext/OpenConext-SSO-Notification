# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:------------------------- |:--------------------------- | 
| Application               | OC SSO Notification Service |
| Version                   | *CURRENT_VERSION*           |
| Changes since version     | *LAST_VERSION*              |
| Release date              | dd-mm-yyyy (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes
<!-- Please note only the stories should be added. -->

|#        | Description                                                   |
|:------- | :------------------------------------------------------------ |
|ENT-XXXX |                                                               |


## Configuration changes

## Known vulnerabilities

This version of the application consists of the following known vulnerabilities:

### spring-web-5.3.24.jar: CVE-2016-1000027 (9.8)

This dependency is used by the latest version of Spring Boot (2.7.6) and yet unresolved. This vulnerability is a false
positive (see: https://github.com/spring-projects/spring-framework/issues/24434#issuecomment-744519525) and added to
the ignore list.

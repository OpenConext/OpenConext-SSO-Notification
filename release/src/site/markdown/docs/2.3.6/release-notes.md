# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 2.3.6                       |
| Changes since version     | 2.3.5                       |
| Release date              | 05-08-2025 (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

| #        | Description                             |
|:---------|:----------------------------------------|
| ENT-5230 | Build release OC SSO Notification 2.3.6 |


## Configuration changes

An additional parameter is added to control the setting of security headers.

    # Security headers - set to false to drop security headers and enhance interoperability between domains
    security.headers.enabled=false

If set to true, the headers will be set and security will be enhanced. This may affect the interoperability of the 
application between domains.

## Known vulnerabilities

At the time of release, this version of the application had no known vulnerabilities with a CVE of 7 or higher.

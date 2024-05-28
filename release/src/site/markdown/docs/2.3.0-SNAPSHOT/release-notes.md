# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 2.3.0-SNAPSHOT              |
| Changes since version     | 2.2.1                       |
| Release date              | dd-mm-yyyy (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

| #        | Description                          |
|:---------|:-------------------------------------|
| ENT-4766 | Remove confederate SSO notification  |
| ENT-4976 | Build release SSO notification 2.3.0 |

## Configuration changes

Support for a confederational use with passthru configuration has been dropped from this version of the OC SSO 
Notification Service. This functionality is no longer used by the client-base, and removing this functionality 
simplifies the codebase. Since this functionality is no longer present, the following configuration can be removed:

    # The secondary endpoint to redirect to if a request fails. Can be used in a confederation setup
    # passthru.endpoint=https://vm.openconext.org/oc-sso-notification/?id={0}&url={1}&redirectUri={2}

## Known vulnerabilities

At the time of release, this version of the application had no known vulnerabilities with a CVE of 7 or higher.

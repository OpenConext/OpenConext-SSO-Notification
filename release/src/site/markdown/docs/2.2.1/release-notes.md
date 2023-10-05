# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 2.2.1                       |
| Changes since version     | 2.2.0                       |
| Release date              | 05-10-2023 (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

| #        | Description                                                                |
|:---------|:---------------------------------------------------------------------------|
| ENT-4200 | Remove old code from OC SSO notification Service                           |
| ENT-4505 | Adapt SSO notification service to support realm in SSO notification cookie |
| ENT-4803 | Build release OC SSO notification service 2.2.1                            |

## Configuration changes

### ENT-4200: Remove old code from OC SSO notification Service
As removing the TGT cookie has no effect on EngineBlock, all features surrounding the tgt cookie have been removed.
This includes the following configuration:

    tgt.cookie.name=tgt_cookie

## New features

### Realm scoping
It is possible to enrich the SAML authentication scoping elements with realms. Realms provide a unique identifier which 
can be used to scope further in the authentication process. For the SSO Notification, a realm can be added to the 
cookie by adding an optional request parameter to the request to the SSO Notification service as follows:

    ...&realm=<realm>

See section Testing the service for a full example of a request.

## Testing the service

Note that you'll need to have a valid Referer HTTP header, so it is necessary to set up a web server running on the 
same domain as the Engineblock application which points to this service. This is needed to be able to place the SSO
Notification cookie. Also do note that cookies will be placed as secure. The Service therefor needs to be accessed
through HTTPS.

Then a request can be sent to the service, for example:

    https://sso.vm.openconext.org?id=my-idp-entityid&url=https://domain.com&redirectUri=https://engine.vm.openconext.org&realm=testRealm

A description for a quick test with a simple HTML page can be found in [test.md](../../docs/test.md).

## Known vulnerabilities

At the time of release, this version of the application had no known vulnerabilities with a CVE of 7 or higher.

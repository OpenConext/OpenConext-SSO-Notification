# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:--------------------------|:----------------------------|
| Application               | OC SSO Notification Service |
| Version                   | 2.2.0                       |
| Changes since version     | 2.1.1                       |
| Release date              | 03-04-2023 (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

| #        | Description                                                             |
|:---------|:------------------------------------------------------------------------|
| ENT-4722 | Implement cache clear functionality                                     |
| ENT-4735 | Build release SSO Notification Service 2.2.0                            |
| ENT-4744 | Update Institution Service & SSO-Notification cache to cache clear hash |

## Configuration changes

With the changes in this version of OC SSO Notification, it is possible to configure the application such that it runs 
more in sync with the data-source. If this is done, OC SSO Notification periodically polls a check-sum endpoint exposed 
on the original data-source. The cache is then evicted if the check-sum has changed since the last time. To do this, 
the following configurations must be configured:

      api.endpoint.url.cacheHash=
      dataservices.fetchCacheHash.cronSchedule=-

Where `api.endpoint.url.cacheHash' is the url (including endpoint) where the check-sum is exposed, and
'dataservices.fetchCacheHash.cronSchedule' is the cron-schedule at which the above endpoint will be polled. This 
schedule is set to '-' by default, which disables this polling feature.

The newly exposed cache-hash endpoint should be added to the configs to fetch the cache-hash:

    api.endpoint.url.cacheHash=http://localhost:3000/api/cache-hash/sso-notification

Furthermore, a cron schedule to retrieve the cache hash once a minute should also be added:

    dataservices.fetchCacheHash.cronSchedule=0 * * * * *

## Known vulnerabilities

At the time of release, this version of the application had no known vulnerabilities with a CVE of 7 or higher.

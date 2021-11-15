# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:------------------------- |:--------------------------- | 
| Application               | OC SSO Notification Service |
| Version                   | 2.0.1                       |
| Changes since version     | 1.0.0                       |
| Release date              | 08-10-2021 (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

|#        | Description                                                                          |
|:------- | :----------------------------------------------------------------------------------- |
|ENT-3773 | Spring Boot update Entree Federatie - SSO notification service                       |
|ENT-3785 | Extend public OC SSO notification service to interface with a Data Services instance |
|ENT-3802 | Build release OC SSO Notification service 2.0.1                                      |
|ENT-3806 | Build SSO Notification service within Github                                         |

## Configuration changes

### ENT-3773 - Spring Boot update Entree Federatie - SSO notification service

The Spring Security User configuration has been altered to secure the /actuator endpoints. The following configuration
should be present:

    # The admin user who can access the management information.
    spring.security.user.name=admin
    spring.security.user.password=secret
    spring.security.user.roles=ACTUATOR
    
    # Actuator expose settings
    management.security.roles=ACTUATOR
    management.endpoints.web.exposure.include=health,info
    management.endpoints.health.roles=ACTUATOR
    management.endpoints.info.roles=ACTUATOR

The lines below can be removed from the configuration

    spring.security.user.roles[0]=USER
    spring.security.user.roles[1]=ACTUATOR

### ENT-3785 - Extend public OC SSO notification service to interface with a Data Services instance

This version of SSO Notification introduces the ability to interface with an external REST API for retrieving SSO 
Notification data instead of using a static file. In order to make the application work with such an API, the following
configuration should be present and populated:

    # API settings
    # Url of the API which returns the SSO notification information by id.
    api.endpoint.url=
    # The name of the API key header
    api.key.header.key=
    # The api-key header value - used in security purposes, is added to idp rest request
    api.key.header.value=
    # The URL-suffix to fetch all SSO Notification data from the Data Service endpoint
    api.endpoint.url.all-suffix=

If this configuration is present, the API will be used to fetch the data. The static file will be used as a fallback 
if the API configuration is not present. In the case the API is not working (either due to wrong configuration or 
outage), the static file will NOT be used as a fallback.

An example of an API response which is expected by the SSO Notification Service is added in "idp.data.json", and should
look like:

    [
        {
            "entityId": "xxx",
            "idpUrlList": [
                "https://*.vm.openconext.org"
            ],
            "redirectUrlList": [
                "https://*.vm.openconext.org"
            ]
        }
    ]

Furthermore, a timeout for connecting to the REST API is introduced. The default value is set to 5 seconds and can be 
configured with:

    connection.timeout.seconds=5
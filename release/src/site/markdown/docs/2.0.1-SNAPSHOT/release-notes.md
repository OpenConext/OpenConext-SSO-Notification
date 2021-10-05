# OC SSO Notification Service - Release Notes

| Field                     | Value                       |
|:------------------------- |:--------------------------- | 
| Application               | OC SSO Notification Service |
| Version                   | 2.0.1-SNAPSHOT              |
| Changes since version     | 1.0.0                       |
| Release date              | dd-mm-2021 (dd-mm-yyyy)     |
| Delivery type             | Full release                |

## Changes

|#        | Description                                                    |
|:------- | :------------------------------------------------------------- |
|ENT-3773 | Spring Boot update Entree Federatie - SSO notification service |


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

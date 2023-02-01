# OC SSO Notification

This Spring Boot Application is created to implement a SSO Notification service for OpenConext.
With a SSO Notification an Identity Provider can be defined which informs Engineblock at which Identity Provider a user 
should log in. This is accomplished by setting a cookie with the entity ID of the Identity Provider. The Engineblock
application retrieves the cookie and initiates an authentication with the set entity ID.

For a more in depth functional description, please refer to the [functional description](release/src/site/markdown/docs/functional-description.md).

The following urls are exposed with this service:

- / - The SSO Notification service
- /actuator/health
- /actuator/info

## Getting started

The OC SSO Notification Service utilises:

- OpenJDK 11
- Maven 3+

## Installation

For development you can start the Spring Boot application with embedded Tomcat from the root of the project using:

    mvn clean install && java -jar oc-sso-notificatie/target/oc-sso-notificatie-*.jar --spring.config.location=release/src/main/resources/sample/config/

## Security checks

Run OWASP security checks by running

    mvn clean install -P security-updates -DskipTests=true -B

## Configuration of SSO Notification service

The application uses key value pairs set in the [application.properties](release/src/main/resources/sample/config/application.properties) 
file.

Settings for exposing inner workings of the application can be configured with the parameters below. Note that for 
Production use, either access to /actuator should be restricted or this configuration should be altered:

    # The admin user who can access the management information.
    spring.security.user.name=admin
    spring.security.user.password=secret
    spring.security.user.roles=ACTUATOR
     
    # Actuator expose settings
    management.security.roles=ACTUATOR
    management.endpoints.web.exposure.include=health,info
    management.endpoints.health.roles=ACTUATOR
    management.endpoints.info.roles=ACTUATOR

With the default configuration, the /actuator/info endpoint can be requested with the following command:

    curl -X GET http://localhost:8083/actuator/info -H 'Authorization: Basic YWRtaW46c2VjcmV0'

Note that the key "YWRtaW46c2VjcmV0" is a Base64 encoded String with format "[username]:[password]", in this case: 
"admin:secret". This value can be generated using https://www.base64encode.org of the Linux command line 
("echo -n 'admin:secret' | base64").

If "management.security.roles" is not set, the actuator endpoint will not be secured.

Settings for the SSO Notification cookie can be configured with the following parameters.

    # The domain to set for the notification cookie
    notification.cookie.domain=vm.openconext.org
    # The path to set for the notification cookie
    notification.cookie.path=/
    # The version to set for the notification cookie
    notification.cookie.version=-1
    # The secure flag to set for the notification cookie
    notification.cookie.secured=true

Settings for the encryption can be configured with the following parameters.

    # The encryption method used
    crypto.encrypt.algorithm=AES/CBC/PKCS5Padding
    # The secure password used to generate the encryption key - used for generating notification cookie value    
    crypto.secure.key=<xxx>
    crypto.secure.key.type=PBKDF2WithHmacSHA256
    crypto.secure.key.algorithm=AES
    # The salt value used to generate the encryption key
    crypto.secure.salt=<xxx>

Since AES-256 is used as the default encryption method, note to set a value of 32 characters for `crypto.secure.key` 
and a value of 16 characters for `crypto.secure.salt`. These settings should also be used in the configuration of 
Engineblock for the decryption process. Please see section below for more details.

For retrieving SSO Notifications there is the option of requesting data from an external REST API which is configured
with:

    # Url of the API which returns the SSO notification information by id.
    api.endpoint.url=
    # The name of the API key header
    api.key.header.key=
    # The api-key header value - used in security purposes, is added to idp rest request
    api.key.header.value=
    # The URL-suffix to fetch all SSO Notification data from the Data Service endpoint
    api.endpoint.url.all-suffix=
    # The amount of seconds before a timeout
    connection.timeout.seconds=5

Alternatively, a data file in JSON format (example found [here](release/src/main/resources/sample/config/idp.data.json)) 
can be configured with:

    data.location=file:release/src/main/resources/sample/config/idp.data.json

If this configuration is present, the API will be used to fetch the data. The static file will be used as a fallback
if the API configuration is not present. In the case the API is not working (either due to wrong configuration or
outage), the static file will NOT be used as a fallback.

For a full example of the configurations, please refer to [Installation Manual](release/src/site/markdown/docs/installation-manual.md).

## Configuration of Engineblock

Reading and processing SSO Notification cookies should be enabled in Engineblock. Furthermore, the encryption key
and salt configured for the SSO Notification service should be configured for Engineblock as well for the decryption
process. Below are the configurations needed in Engineblock.

    feature_enable_sso_notification: true
    sso_notification_encryption_algorithm: AES-256-CBC
    sso_notification_encryption_key: <xxx>
    sso_notification_encryption_key_salt: <xxx>

## Testing the service

Note that you'll need to have a valid Referer HTTP header, so it is necessary to set up a web server running on the 
same domain as the Engineblock application which points to this service. This is needed to be able to place the SSO 
Notification cookie. Also do note that cookies will be placed as secure. The Service therefor needs to be accessed 
through HTTPS.

Then a request can be sent to the service, for example:

    https://sso.vm.openconext.org?id=my-idp-entityid&url=https://domain.com&redirectUri=https://engine.vm.openconext.org

A description for a quick test with a simple HTML page can be found in [test.md](release/src/site/markdown/docs/test.md).

## License

OC SSO Notification - [Apache License, Version 2.0](LICENSE-2.0.txt)

## Contact
For more information, contact [Stichting Kennisnet](mailto:implementaties@kennisnet.nl).

**Copyright(c) 2021 [Stichting Kennisnet]**

[//]: # (These are reference links used in the body of this note)
   [Stichting Kennisnet]: <http://www.kennisnet.nl>

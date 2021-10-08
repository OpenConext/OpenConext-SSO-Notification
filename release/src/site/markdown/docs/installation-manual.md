# Installation manual

The OC SSO Notification Service uses:

- OpenJDK 11
- Tomcat 9

Please note Tomcat 9 is embedded within the delivery.

All the installation examples assume the root user is used to install.

## OpenJDK 11

The version of Java used is OpenJDK 11.x.

### Example to install OpenJDK 11
    apt-get install openjdk-11-jdk

	update-alternatives --install /usr/bin/java java /opt/jdk-11/bin/java 100
    update-alternatives --install /usr/bin/javac javac /opt/jdk-11/bin/javac 100

### Build the application

Build the application by running the following command from the root of the project.

    mvn clean install

### Environment specific configuration files

The environment specific (configuration) files should be managed/set by the owner of the environment.

The application environment uses an application.properties file. A sample configuration file is provided in the
release directory.
The following values need to be changed per environment.

- notification.cookie.domain
- crypto.secure.key
- crypto.secure.salt

Example application.properties file

    # API settings
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

    # SSO Notification cookie settings
    # The domain to set for the notification cookie
    notification.cookie.domain=vm.openconext.org
    # The path to set for the notification cookie
    notification.cookie.path=/
    # The version to set for the notification cookie
    notification.cookie.version=-1
    # The secure flag to set for the notification cookie
    notification.cookie.secured=true
    
    # The name of the tgt cookie set by the authentication server.
    tgt.cookie.name=tgt_cookie
    
    # Crypto related settings
    # The encryption method used
    crypto.encrypt.algorithm=AES/CBC/PKCS5Padding
    # The secure password used to generate the encryption key - used for generating notification cookie value
    crypto.secure.key=axNEgHbZrcQQQ39MGNBMczcuDRN9WLv3
    crypto.secure.key.type=PBKDF2WithHmacSHA256
    crypto.secure.key.algorithm=AES
    # The salt value used to generate the encryption key
    crypto.secure.salt=cQ_Jym5aH$Fqua#D
    
    # Caching settings
    # The expiration time in seconds to invalidate the cached entries for the retrieved idp information.
    idp.cache.expiration.time.seconds=300
    
    # The admin user who can access the management information.
    spring.security.user.name=admin
    # The password should be a bcrypt hash
    spring.security.user.password=<xxx>
    spring.security.user.roles=ACTUATOR

    # Actuator expose settings
    management.security.roles=ACTUATOR
    management.endpoints.web.exposure.include=health,info
    management.endpoints.health.roles=ACTUATOR
    management.endpoints.info.roles=ACTUATOR
    
    # Logging settings
    
    # See https://docs.spring.io/spring-boot/docs/current/reference/html/howto-logging.html#howto-configure-logback-for-logging
    # for logging properties
    
    # The location to the logback-spring.xml configuration file, for example /conf/logback-spring.xml within Docker.
    logging.config=release/src/main/resources/sample/config/logback-spring.xml
    
    # Additional logging properties which are supported by Spring, as alternative to configure these in the the logback-spring.xml file.
    # see http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html#boot-features-custom-log-levels
    
    # Uncomment the next line to and specify the location to the logfile if the log should not only be added in the console but in a file as well.
    #logging.file=/var/log/sso-notification/sso-notification.log
    
    data.location=file:release/src/main/resources/sample/config/idp.data.json

#### logback-spring.xml

The application uses a logback-spring.xml configuration file defined as logging.config property to define the logging.

Example logback-spring.xml file

    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <include resource="org/springframework/boot/logging/logback/base.xml"/>
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>oc-sso-notification-eventlog-dev.log</file>
            <append>true</append>
            <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
                <level>INFO</level>
            </filter>
    
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <!-- daily rollover -->
                <fileNamePattern>oc-sso-notification-eventlog-dev.%d{yyyy-MM-dd}.log</fileNamePattern>
    
                <!-- keep 30 days' worth of history capped at 1GB total size -->
                <maxHistory>30</maxHistory>
                <totalSizeCap>1GB</totalSizeCap>
            </rollingPolicy>
    
            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                <!-- Disable field names to match the log pattern of the hub -->
                <fieldNames>
                    <timestamp>timestamp</timestamp>
                    <message>message</message>
                    <version>[ignore]</version>
                    <thread>[ignore]</thread>
                    <logger>[ignore]</logger>
                    <level>[ignore]</level>
                    <levelValue>[ignore]</levelValue>
                </fieldNames>
            </encoder>
        </appender>
    
        <logger name="oc-sso-eventlogger" level="info" additivity="false">
            <appender-ref ref="FILE" />
        </logger>
    </configuration>

### Running the application

The application is packaged as a JAR. The application can be started with the command:

    java -jar oc-sso-notificatie/target/oc-sso-notificatie-<version>.jar --spring.config.location=release/src/main/resources/dev/config/
    
Change the version according to the version built and optionally change the location of the configuration files.
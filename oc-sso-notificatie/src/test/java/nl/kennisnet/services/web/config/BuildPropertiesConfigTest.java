package nl.kennisnet.services.web.config;

import nl.kennisnet.services.config.BuildPropertiesConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BuildPropertiesConfigTest {

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private final BuildPropertiesConfig buildPropertiesConfig = new BuildPropertiesConfig();


    @Test
    void buildPropertiesMissing() {
        Resource mockResource = mock(Resource.class);
        when(mockResource.exists()).thenReturn(false);
        when(resourceLoader.getResource(any())).thenReturn(mockResource);

        BuildProperties buildProperties = buildPropertiesConfig.buildProperties(resourceLoader);

        assertEquals("0.0.0-TEST", buildProperties.getVersion());
        assertEquals("test-build", buildProperties.getName());
        assertEquals("test-group", buildProperties.getGroup());
        assertEquals("test-artifact", buildProperties.getArtifact());
    }

    @Test
    void buildPropertiesAvailable() throws IOException {
        Resource mockResource = mock(Resource.class);

        when(mockResource.exists()).thenReturn(true);
        when(mockResource.getInputStream()).thenReturn(
                new ByteArrayInputStream((
                        """
                                build.artifact=oc-sso-notificatie
                                build.group=nl.kennisnet.services
                                build.java_version=21
                                build.name=OC SSO Notification - Application
                                build.spring_boot_version=4.0.5
                                build.time=2026-04-13T10\\:35\\:03.571Z
                                build.version=2.5.0""").getBytes()));

        when(resourceLoader.getResource(any())).thenReturn(mockResource);

        BuildProperties buildProperties = buildPropertiesConfig.buildProperties(resourceLoader);

        assertEquals("oc-sso-notificatie", buildProperties.getArtifact());
        assertEquals("nl.kennisnet.services", buildProperties.getGroup());
        assertEquals("OC SSO Notification - Application", buildProperties.getName());
        assertEquals("2.5.0", buildProperties.getVersion());
        assertEquals(Instant.parse("2026-04-13T10:35:03.571Z"), buildProperties.getTime());
        assertEquals("21", buildProperties.get("java_version"));
        assertEquals("4.0.5", buildProperties.get("spring_boot_version"));
    }

    @Test
    void buildPropertiesMissingIOException() throws IOException {
        Resource mockResource = mock(Resource.class);

        when(mockResource.exists()).thenReturn(true);
        doThrow(IOException.class).when(mockResource).getInputStream();

        when(resourceLoader.getResource(any())).thenReturn(mockResource);

        BuildProperties buildProperties = buildPropertiesConfig.buildProperties(resourceLoader);

        assertEquals("0.0.0-ERROR", buildProperties.getVersion());
        assertEquals("error-build", buildProperties.getName());
        assertEquals("error-group", buildProperties.getGroup());
        assertEquals("error-artifact", buildProperties.getArtifact());
    }

}

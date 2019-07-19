package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TaskProxyConfigurationTest {

    public static final String ENV_HTTP_PROXY = "http_proxy";
    public static final String ENV_HTTPS_PROXY = "https_proxy";
    @Mock
    private ReadyCIConfiguration readyCIConfiguration;

    @InjectMocks
    private TaskProxyConfiguration subject;

    private ProcessBuilder processBuilder;

    @Before
    public void setup() {
        processBuilder = new ProcessBuilder();
        Map environment = processBuilder.environment();
        // Ensure we don't pick up existing environment configuration by clearing out proxy settings.
        environment.remove(ENV_HTTP_PROXY);
        environment.remove(ENV_HTTPS_PROXY);
    }

    @Test
    public void testFullConfigurationWithURLEncoding() {
        setupConfiguration("proxyHost1", "proxyPort1", "proxyUsername@!1", "proxyPassword!@1");
        subject.configureProxyServer(processBuilder, readyCIConfiguration);
        String httpProxyLine = processBuilder.environment().get(ENV_HTTP_PROXY);
        String httpsProxyLine = processBuilder.environment().get(ENV_HTTPS_PROXY);
        assertEquals("http proxy line works with username and password", "http://proxyUsername%40%211:proxyPassword%21%401@proxyHost1:proxyPort1", httpProxyLine);
        assertEquals("https proxy line works with username and password", "http://proxyUsername%40%211:proxyPassword%21%401@proxyHost1:proxyPort1", httpsProxyLine);
    }

    @Test
    public void testConfigurationHostAndPortOnly() {
        setupConfiguration("proxyHost2", "proxyPort2", "", "");
        subject.configureProxyServer(processBuilder, readyCIConfiguration);
        String httpProxyLine = processBuilder.environment().get(ENV_HTTP_PROXY);
        String httpsProxyLine = processBuilder.environment().get(ENV_HTTPS_PROXY);
        assertEquals("http proxy line is correct with only host and port", "http://proxyHost2:proxyPort2", httpProxyLine);
        assertEquals("https proxy line is correct with only host and port", "http://proxyHost2:proxyPort2", httpsProxyLine);
    }

    @Test
    public void testEmptyConfiguration() {
        setupConfiguration("", "", "", "");
        subject.configureProxyServer(processBuilder, readyCIConfiguration);
        String httpProxyLine = processBuilder.environment().get("ENV_HTTP_PROXY");
        String httpsProxyLine = processBuilder.environment().get("ENV_HTTPS_PROXY");
        assertEquals("http proxy line is null when configuration is not specified", null, httpProxyLine);
        assertEquals("https proxy line is null when configuration is not specified", null, httpsProxyLine);
    }

    private void setupConfiguration(String proxyHost, String proxyPort, String proxyUsername, String proxyPassword) {
        Mockito.when(readyCIConfiguration.getProxyHost()).thenReturn(proxyHost);
        Mockito.when(readyCIConfiguration.getProxyPort()).thenReturn(proxyPort);
        Mockito.when(readyCIConfiguration.getProxyUsername()).thenReturn(proxyUsername);
        Mockito.when(readyCIConfiguration.getProxyPassword()).thenReturn(proxyPassword);
    }

}

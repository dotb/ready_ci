package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class TaskProxyConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskProxyConfiguration.class);

    public void configureProxyServer(ProcessBuilder processBuilder, ReadyCIConfiguration configuration) {
        String proxyConfString = getProxyConfString(configuration);
        if (!proxyConfString.isEmpty()) {
            Map<String, String> environment = processBuilder.environment();
            environment.put("http_proxy", proxyConfString);
            environment.put("https_proxy", proxyConfString);
        }
    }

    private String getProxyConfString(ReadyCIConfiguration configuration) {
        String proxyHost = configuration.getProxyHost();
        String proxyPort = configuration.getProxyPort();
        try {
            String proxyUsername = URLEncoder.encode(configuration.getProxyUsername(), "UTF-8");
            String proxyPassword = URLEncoder.encode(configuration.getProxyPassword(), "UTF-8");

            if (!proxyHost.isEmpty() && !proxyPort.isEmpty() && !proxyUsername.isEmpty() && !proxyPassword.isEmpty()) {
                LOGGER.debug("Configuring proxy {} {} with username and password", proxyHost, proxyPort);
                return String.format("http://%s:%s@%s:%s", proxyUsername, proxyPassword, proxyHost, proxyPort);
            } else if (!proxyHost.isEmpty() && !proxyPort.isEmpty()) {
                LOGGER.debug("Configuring proxy {} {}", proxyHost, proxyPort);
                return String.format("http://%s:%s", proxyHost, proxyPort);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error while configure the proxy server {} {}", proxyHost, e.toString());
        }
        LOGGER.debug("Not configuring proxy");
        return "";
    }

}

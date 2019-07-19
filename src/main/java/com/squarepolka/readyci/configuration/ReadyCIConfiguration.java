package com.squarepolka.readyci.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squarepolka.readyci.configuration.parameter.ParsedParameter;
import com.squarepolka.readyci.configuration.parameter.ParameterParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the parsing and storage of multiple pipeline configurations
 */
public class ReadyCIConfiguration {

    public static final String ARG_SERVER = "server";
    public static final String ARG_PIPELINE = "pipeline=";
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyCIConfiguration.class);
    private static ReadyCIConfiguration instance;

    private boolean isServerMode;
    private String instanceName;
    private String proxyHost;
    private String proxyPort;
    private String proxyUsername;
    private String proxyPassword;
    private List<PipelineConfiguration> pipelines;
    private PipelineConfiguration pipelineToRun;

    public static ReadyCIConfiguration instance() {
        if (null == instance) {
            instance = new ReadyCIConfiguration();
        }
        return instance;
    }

    private ReadyCIConfiguration() {
        this.instanceName = "ReadyCI";
        this.isServerMode = false;
        this.proxyHost = "";
        this.proxyPort = "";
        this.proxyUsername = "";
        this.proxyPassword = "";
        this.pipelines = new ArrayList<>();
        this.pipelineToRun = null;
    }

    /**
     * Use the specified pipelineName to return a pipeline configuration.
     * @param pipelineName
     * @return the associated pipeline
     */
    public PipelineConfiguration getPipeline(String pipelineName) {
        for (PipelineConfiguration pipeline : pipelines) {
            if (pipeline.getName().equalsIgnoreCase(pipelineName)) {
                return pipeline;
            }
        }
        throw new LoadConfigurationException(String.format("A pipeline configuration for the pipeline name, %s, does not exist", pipelineName));
    }

    public List<PipelineConfiguration> getPipelines(String repositoryName, String branch) {
        List<PipelineConfiguration> matchedPipelines = new ArrayList<>();
        for (PipelineConfiguration pipeline : pipelines) {
            if (pipeline.matchesRepositoryName(repositoryName, branch)) {
                matchedPipelines.add(pipeline);
            }
        }
        return matchedPipelines;
    }

    public void handleInputArguments(String[] arguments) {
        for (String argument : arguments) {
            handleInputArgument(argument);
        }
    }

    private void handleInputArgument(String argument) {
        if (argument.contains(".yml")) {
            loadConfigurationFile(argument);
        } else if (argument.equalsIgnoreCase(ARG_SERVER)) {
            isServerMode = true;
        } else if (argument.startsWith(ARG_PIPELINE)) {
            customisePipelineToRun(argument);
        } else {
            addParameterToAllPipelines(argument);
        }
    }

    public static ReadyCIConfiguration readConfigurationFile(String configurationFileName) {
        File configurationFile = new File(configurationFileName);
        return readConfigurationFile(configurationFile);
    }

    public static ReadyCIConfiguration readConfigurationFile(File configurationFile) {
        YAMLFactory yamlFactory = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yamlFactory);
        try {
            return mapper.readValue(configurationFile, ReadyCIConfiguration.class);
        } catch (Exception e) {
            LoadConfigurationException configurationException = new LoadConfigurationException(String.format("Could not load configuration from %s: %s", configurationFile.getAbsolutePath(), e.toString()));
            configurationException.setStackTrace(e.getStackTrace());
            throw configurationException;
        }
    }

    private void loadConfigurationFile(String fileName) {
            ReadyCIConfiguration newConfiguration = readConfigurationFile(fileName);
            this.instanceName = mergeStringProperty(this.instanceName, newConfiguration.instanceName);
            this.isServerMode = newConfiguration.isServerMode;
            this.proxyHost = mergeStringProperty(this.proxyHost, newConfiguration.proxyHost);
            this.proxyPort = mergeStringProperty(this.proxyPort, newConfiguration.proxyPort);
            this.proxyUsername = mergeStringProperty(this.proxyUsername, newConfiguration.proxyUsername);
            this.proxyPassword = mergeStringProperty(this.proxyPassword, newConfiguration.proxyPassword);
            this.pipelines = newConfiguration.pipelines;
            LOGGER.info("Loaded configuration {} with {} pipelines", fileName, pipelines.size());
    }

    private String mergeStringProperty(String oldValue, String newValue) {
        if (null != newValue && newValue.length() > 0) {
            return newValue;
        } else {
            return oldValue;
        }
    }
    
    private void customisePipelineToRun(String pipelineNameArgument) {
        ParsedParameter parsedParameter = new ParsedParameter(pipelineNameArgument);
        String pipelineToRunName = parsedParameter.getParameterValue();
        PipelineConfiguration pipelineConfigurationToRun;
        try {
            pipelineConfigurationToRun = getPipeline(pipelineToRunName);
        } catch (LoadConfigurationException e) {
            pipelineConfigurationToRun = new PipelineConfiguration();
            pipelineConfigurationToRun.setName(pipelineToRunName);
            pipelines.add(pipelineConfigurationToRun);
        }
        pipelineToRun = pipelineConfigurationToRun;
    }

    private void addParameterToAllPipelines(String parameterArgument) {
        try {
            ParsedParameter parsedParameter = new ParsedParameter(parameterArgument);
            for (PipelineConfiguration pipelineConfiguration : pipelines) {
                pipelineConfiguration.setParameter(parsedParameter.getParameterKey(), parsedParameter.getParameterValue());
            }
        } catch (ParameterParseException e){
            LOGGER.warn(e.toString());
        }
    }

    // Getters
    public static ReadyCIConfiguration getInstance() {
        return instance;
    }

    public boolean isServerMode() {
        return isServerMode;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public PipelineConfiguration getPipelineToRun() {
        return pipelineToRun;
    }

    // Setters
    public static void setInstance(ReadyCIConfiguration instance) {
        ReadyCIConfiguration.instance = instance;
    }

    public void setServerMode(boolean serverMode) {
        isServerMode = serverMode;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public void setPipelines(List<PipelineConfiguration> pipelines) {
        this.pipelines = pipelines;
    }
}

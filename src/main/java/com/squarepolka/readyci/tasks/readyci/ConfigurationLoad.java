package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.configuration.LoadConfigurationException;
import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class ConfigurationLoad extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLoad.class);
    public static final String TASK_CONFIGURATION_LOAD = "readyci_configuration_load";
    public static final String TASK_CONFIGURATION_FILE_NAME = "readyci.yml";

    @Override
    public String taskIdentifier() {
        return TASK_CONFIGURATION_LOAD;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        File repoConfigurationFile = getRepoConfigurationFile(buildEnvironment);
        if (repoConfigurationFile.exists()) {
            LOGGER.debug("Loading local repository configuration from {}", TASK_CONFIGURATION_FILE_NAME);
            ReadyCIConfiguration localConfiguration = ReadyCIConfiguration.readConfigurationFile(repoConfigurationFile);
            mergeLocalConfigWithBuildEnvironment(localConfiguration, buildEnvironment);
        } else {
            LOGGER.debug("Local repository configuration {} not found. Repository configuration is not being used.", TASK_CONFIGURATION_FILE_NAME);
        }
    }
    
    private File getRepoConfigurationFile(BuildEnvironment buildEnvironment) {
        String localConfigurationPath = String.format("%s/%s", buildEnvironment.getProjectPath(), TASK_CONFIGURATION_FILE_NAME);
        return new File(localConfigurationPath);
    }

    private void mergeLocalConfigWithBuildEnvironment(ReadyCIConfiguration localConfiguration, BuildEnvironment buildEnvironment) {
        String pipelineName = buildEnvironment.getPipelineName();
        try {
            PipelineConfiguration repoPipelineConf = localConfiguration.getPipeline(pipelineName);
            buildEnvironment.getProjectFolderFromConfiguration(repoPipelineConf);
            buildEnvironment.configureProjectPath();
            buildEnvironment.getCredentialsPathFromConfiguration(repoPipelineConf);
            buildEnvironment.setBuildParameters(repoPipelineConf);

            List<Task> configuredTasks = taskRunner.getTaskRunnerFactory().createTaskListFromConfig(repoPipelineConf.getTasks());
            LOGGER.debug("Loaded {} tasks from the repository configuration {}", configuredTasks.size(), TASK_CONFIGURATION_FILE_NAME);
            taskRunner.setConfiguredTasks(configuredTasks);
        } catch (LoadConfigurationException e) {
            LOGGER.debug("We found a local readyci configuration file in the project folder but it didn't contain a definition for the {} pipeline", pipelineName);
        }
    }

}
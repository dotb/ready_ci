package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TaskCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCommand.class);
    private ArrayList<String> commandAndParams;
    private BuildEnvironment buildEnvironment;

    public TaskCommand(BuildEnvironment buildEnvironment) {
        this.buildEnvironment = buildEnvironment;
        this.commandAndParams = new ArrayList<>();
    }

    public TaskCommand(String[] command) {
        this.commandAndParams = new ArrayList<>();
        for (int i = 0; i < command.length; i++) {
            String parameter = command[i];
            this.commandAndParams.add(parameter);
        }
    }

    /**
     * Add a string to the list of command parameters
     * @param command - the command to add, for example ls
     * @return TaskCommand - so that you can add more parameter functions
     */
    public TaskCommand addStringCommand(String command) {
        commandAndParams.add(command);
        return this;
    }

    /**
     * Add an environment parameter to the list of command line parameters e.g. -Duser=jane
     * @param commandLineKey - command line key. e.g. -Duser
     * @param configurationKey - the environment key used to elicit the value configured in the environment e.g. userName, which might be set to 'jane'
     * @return TaskCommand - so that you can add more parameter functions, with the command added. E.g -Duser=jane
     */
    public TaskCommand addEnvironmentParameter(String commandLineKey, String environmentKey) {
        try {
            Object parameterValue = buildEnvironment.getObject(environmentKey);
            commandAndParams.add(commandLineKey + "=" + parameterValue);
        } catch (PropertyMissingException e) {
            LOGGER.debug("The String parameter {} is not available. Not adding it to the command", environmentKey);
        }
        return this;
    }

    /**
     * This function will pull a value specified in the build environment and place it into the list of command line parameters
     * if a specified boolean configuration value is true
     * @param commandLineKey - the key that should be placed into the list of command line parameters
     * @param configurationKey - the key for the parameter specified in the yml configuration
     * @param environmentKey - the key used to elicit a value stored in the build environment
     * @return TaskCommand - so that you can add more parameter functions
     */
    public TaskCommand addEnvironmentParameterIfConfiguredParamIsTrue(String commandLineKey, String configurationKey, String environmentKey) {
        try {
            boolean configuredValue = buildEnvironment.getSwitch(configurationKey);
            if (configuredValue) {
                Object parameterValue = buildEnvironment.getObject(environmentKey);
                commandAndParams.add(commandLineKey + "=" + parameterValue);
            }
        } catch (PropertyMissingException e) {
            LOGGER.debug("The boolean parameter {} is not available. Not adding it to the command", configurationKey);
        }
        return this;
    }

    public List<String> getCommandAndParams() {
        return commandAndParams;
    }

}

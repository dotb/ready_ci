package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class TaskCommandTest {

    private static final String TEST_COMMAND_SINGLE_STRING = "user=jane";
    private static final String TEST_COMMAND_KEY = "testCommandKey";
    private static final String TEST_BUILD_ENV_KEY_MISSING = "testBuildEnvKeyMissing";
    private static final String TEST_KEY_STRING = "testKeyString";
    private static final String TEST_KEY_STRING_LIST = "testKeyStringList";
    private static final String TEST_KEY_BOOLEAN_TRUE = "testKeyBooleanTrue";
    private static final String TEST_KEY_BOOLEAN_FALSE = "testKeyBooleanFalse";
    private static final String TEST_VALUE_STRING = "testValue";
    private static final Boolean TEST_VALUE_BOOLEAN_TRUE = true;
    private static final Boolean TEST_VALUE_BOOLEAN_FALSE = false;

    public PipelineConfiguration pipelineConfiguration;

    private BuildEnvironment buildEnvironment;

    private TaskCommand subject;

    @Before
    public void setup() {
        pipelineConfiguration = new PipelineConfiguration();
        List<String> testStringList = new LinkedList<>();
        testStringList.add("string1");
        testStringList.add("string2");
        testStringList.add("string3");

        pipelineConfiguration.setParameter(TEST_KEY_STRING, TEST_VALUE_STRING);
        pipelineConfiguration.setParameter(TEST_KEY_STRING_LIST, testStringList);
        pipelineConfiguration.setParameter(TEST_KEY_BOOLEAN_TRUE, TEST_VALUE_BOOLEAN_TRUE);
        pipelineConfiguration.setParameter(TEST_KEY_BOOLEAN_FALSE, TEST_VALUE_BOOLEAN_FALSE);

        buildEnvironment = new BuildEnvironment(pipelineConfiguration);
        subject = new TaskCommand(buildEnvironment);
    }


    @Test
    public void addStringCommand() {
        subject.addStringCommand(TEST_COMMAND_SINGLE_STRING);
        List commandAndParams = subject.getCommandAndParams();
        assertEquals("A single string command is added properly", TEST_COMMAND_SINGLE_STRING, commandAndParams.get(0));
    }

    @Test
    public void addStringParameter() {
        subject.addEnvironmentParameter(TEST_COMMAND_KEY, TEST_KEY_STRING);
        List commandAndParams = subject.getCommandAndParams();
        String expectedCommandString = TEST_COMMAND_KEY + "=" + TEST_VALUE_STRING;
        assertEquals("An environment parameter is added properly", expectedCommandString, commandAndParams.get(0));
    }

    @Test
    public void addParameterThatIsMissing() {
        subject.addEnvironmentParameter(TEST_COMMAND_KEY, TEST_BUILD_ENV_KEY_MISSING);
        List commandAndParams = subject.getCommandAndParams();
        assertEquals("A missing parameter is not added and the list of commands is empty", 0, commandAndParams.size());
    }

    @Test
    public void addBooleanParameterThatIsTrue() {
        subject.addEnvironmentParameter(TEST_COMMAND_KEY, TEST_KEY_BOOLEAN_TRUE);
        List commandAndParams = subject.getCommandAndParams();
        String expectedCommandString = TEST_COMMAND_KEY + "=true";
        assertEquals("An boolean environment parameter set to true is added properly", expectedCommandString, commandAndParams.get(0));
    }

    @Test
    public void addBooleanParameterThatIsFalse() {
        subject.addEnvironmentParameter(TEST_COMMAND_KEY, TEST_KEY_BOOLEAN_FALSE);
        List commandAndParams = subject.getCommandAndParams();
        String expectedCommandString = TEST_COMMAND_KEY + "=false";
        assertEquals("An boolean environment parameter set to false is added properly", expectedCommandString, commandAndParams.get(0));
    }

    @Test
    public void addBooleanEnvironmentParameterIfConfiguredParamIsTrueAndTrueValueConfigured() {
        subject.addEnvironmentParameterIfConfiguredParamIsTrue(TEST_COMMAND_KEY, TEST_KEY_BOOLEAN_TRUE, TEST_KEY_BOOLEAN_TRUE);
        List commandAndParams = subject.getCommandAndParams();
        String expectedCommandString = TEST_COMMAND_KEY + "=" + "true";
        assertEquals("A boolean environment parameter set to true is added properly", expectedCommandString, commandAndParams.get(0));
    }

    @Test
    public void addBooleanEnvironmentParameterIfConfiguredParamIsTrueAndFalseValueConfigured() {
        subject.addEnvironmentParameterIfConfiguredParamIsTrue(TEST_COMMAND_KEY, TEST_KEY_BOOLEAN_TRUE, TEST_KEY_BOOLEAN_FALSE);
        List commandAndParams = subject.getCommandAndParams();
        String expectedCommandString = TEST_COMMAND_KEY + "=" + "false";
        assertEquals("A boolean environment parameter set to false is added properly", expectedCommandString, commandAndParams.get(0));
    }

    @Test
    public void addBooleanEnvironmentParameterIfConfiguredParamIsFalse() {
        subject.addEnvironmentParameterIfConfiguredParamIsTrue(TEST_COMMAND_KEY, TEST_KEY_BOOLEAN_FALSE, TEST_KEY_BOOLEAN_FALSE);
        List commandAndParams = subject.getCommandAndParams();
        assertEquals("The command should be empty if the configuration parameter is false.", 0, commandAndParams.size());
    }

    @Test
    public void addStringEnvironmentParameterIfConfiguredParamIsTrue() {
        subject.addEnvironmentParameterIfConfiguredParamIsTrue(TEST_COMMAND_KEY, TEST_KEY_BOOLEAN_TRUE, TEST_KEY_STRING);
        List commandAndParams = subject.getCommandAndParams();
        String expectedCommandString = TEST_COMMAND_KEY + "=" + TEST_VALUE_STRING;
        assertEquals("A boolean environment parameter set to false is added properly", expectedCommandString, commandAndParams.get(0));
    }

    @Test
    public void addStringEnvironmentParameterIfConfiguredParamIsTrueAndFalseValueConfigured() {
        subject.addEnvironmentParameterIfConfiguredParamIsTrue(TEST_COMMAND_KEY, TEST_KEY_BOOLEAN_FALSE, TEST_KEY_STRING);
        List commandAndParams = subject.getCommandAndParams();
        assertEquals("The command should be empty if the configuration parameter is false.", 0, commandAndParams.size());
    }

    @Test
    public void addEnvironmentParameterIfConfiguredParamIsTrueWithMissingConfigurationKey() {
        subject.addEnvironmentParameterIfConfiguredParamIsTrue(TEST_COMMAND_KEY, TEST_BUILD_ENV_KEY_MISSING, TEST_KEY_STRING);
        List commandAndParams = subject.getCommandAndParams();
        assertEquals("A missing parameter means nothing is added and the list of commands, which remains empty", 0, commandAndParams.size());
    }

    @Test
    public void addEnvironmentParameterIfConfiguredParamIsTrueWithMissingEnvironmentKey() {
        subject.addEnvironmentParameterIfConfiguredParamIsTrue(TEST_COMMAND_KEY, TEST_KEY_BOOLEAN_TRUE, TEST_BUILD_ENV_KEY_MISSING);
        List commandAndParams = subject.getCommandAndParams();
        assertEquals("A missing environment parameter means nothing is added and the list of commands, which remains empty", 0, commandAndParams.size());
    }

}
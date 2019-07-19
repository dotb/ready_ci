package com.squarepolka.readyci.tasks.quality;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.readyci.TaskCommandHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.squarepolka.readyci.tasks.quality.SonarqubeRunner.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SonarqubeRunnerTest {

    private static String PARAM_SONAR = "sonar-scanner";
    private static String PARAM_SONAR_URL_KEY = "-Dsonar.host.url";
    private static String PARAM_SONAR_URL_VALUE = "https://127.0.0.1:1234";
    private static String PARAM_SONAR_LOGIN_KEY = "-Dsonar.login";
    private static String PARAM_SONAR_LOGIN_VALUE = "sonarLogin";
    private static String PARAM_SONAR_PROJECT_KEY = "-Dsonar.projectKey";
    private static String PARAM_SONAR_PROJECT_VALUE = "projectKey";
    private static String PARAM_SONAR_SOURCES_KEY = "-Dsonar.sources";
    private static String PARAM_SONAR_SOURCES_VALUE = "/path/to/src";
    private static String PARAM_SONAR_BINARY_KEY = "-Dsonar.java.binaries";
    private static String PARAM_SONAR_BINARY_VALUE = "/path/to/binary";
    private static String PARAM_SONAR_TESTS_KEY = "-Dsonar.tests";
    private static String PARAM_SONAR_TESTS_VALUE = "/path/to/tests";
    private static String PARAM_SONAR_JAVA_COVERAGE_KEY = "-Dsonar.java.coveragePlugin";
    private static String PARAM_SONAR_JAVA_COVERAGE_VALUE = "javaCoveragePlugin";
    private static String PARAM_SONAR_JACOCO_REP_PATH_KEY = "-Dsonar.jacoco.reportPath";
    private static String PARAM_SONAR_JACOCO_REP_PATH_VALUE = "/path/to/coverage/reports";
    private static String PARAM_SONAR_WRAPPER_BYPASS_KEY = "-Dsonar.cfamily.build-wrapper-output.bypass";
    private static String PARAM_SONAR_WRAPPER_BYPASS_VALUE = "true";

    @Mock
    private BuildEnvironment buildEnvironment;

    @Mock
    private TaskCommandHandler taskCommandHandler;

    @InjectMocks
    private SonarqubeRunner subject;

    @Test
    public void testIdentifier() {
        String identifier = subject.taskIdentifier();
        assertEquals("The sonarqune identifier is correct", "sonarqube_runner", identifier);
    }

    @Test
    public void testPerformTask() {

        String expectedWorkingDirectory = "workingDir";
        List<String> expectedCommand = new ArrayList<>();
        expectedCommand.add(PARAM_SONAR);
        expectedCommand.add(PARAM_SONAR_URL_KEY + "=" + PARAM_SONAR_URL_VALUE);
        expectedCommand.add(PARAM_SONAR_LOGIN_KEY + "=" + PARAM_SONAR_LOGIN_VALUE);
        expectedCommand.add(PARAM_SONAR_PROJECT_KEY + "=" + PARAM_SONAR_PROJECT_VALUE);
        expectedCommand.add(PARAM_SONAR_SOURCES_KEY + "=" + PARAM_SONAR_SOURCES_VALUE);
        expectedCommand.add(PARAM_SONAR_BINARY_KEY + "=" + PARAM_SONAR_BINARY_VALUE);
        expectedCommand.add(PARAM_SONAR_TESTS_KEY + "=" + PARAM_SONAR_TESTS_VALUE);
        expectedCommand.add(PARAM_SONAR_JAVA_COVERAGE_KEY + "=" + PARAM_SONAR_JAVA_COVERAGE_VALUE);
        expectedCommand.add(PARAM_SONAR_JACOCO_REP_PATH_KEY + "=" + PARAM_SONAR_JACOCO_REP_PATH_VALUE);
        expectedCommand.add(PARAM_SONAR_WRAPPER_BYPASS_KEY + "=" + PARAM_SONAR_WRAPPER_BYPASS_VALUE);

        Mockito.when(buildEnvironment.getProjectPath()).thenReturn(expectedWorkingDirectory);
        Mockito.when(buildEnvironment.getObject("sonarHostUrl")).thenReturn("url");
        Mockito.when(buildEnvironment.getObject(BUILD_PROP_SONAR_HOST_URL)).thenReturn(PARAM_SONAR_URL_VALUE);
        Mockito.when(buildEnvironment.getObject(BUILD_PROP_SONAR_LOGIN_KEY)).thenReturn(PARAM_SONAR_LOGIN_VALUE);
        Mockito.when(buildEnvironment.getObject(BUILD_PROP_SONAR_PROJECT_KEY)).thenReturn(PARAM_SONAR_PROJECT_VALUE);
        Mockito.when(buildEnvironment.getObject(BUILD_PROP_SONAR_SOURCE_PATH)).thenReturn(PARAM_SONAR_SOURCES_VALUE);
        Mockito.when(buildEnvironment.getObject(BUILD_PROP_SONAR_BINARY_PATH)).thenReturn(PARAM_SONAR_BINARY_VALUE);
        Mockito.when(buildEnvironment.getObject(BUILD_PROP_SONAR_TEST_PATH)).thenReturn(PARAM_SONAR_TESTS_VALUE);
        Mockito.when(buildEnvironment.getObject(BUILD_PROP_SONAR_JAVA_COVERAGE_PLUGIN)).thenReturn(PARAM_SONAR_JAVA_COVERAGE_VALUE);
        Mockito.when(buildEnvironment.getObject(BUILD_PROP_SONAR_JACOCO_REPORT_PATH)).thenReturn(PARAM_SONAR_JACOCO_REP_PATH_VALUE);
        Mockito.when(buildEnvironment.getObject(BUILD_PROP_SONAR_BYPASS_BUILD_WRAPPER)).thenReturn(PARAM_SONAR_WRAPPER_BYPASS_VALUE);

        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, expectedWorkingDirectory);
    }

}
package com.squarepolka.readyci.tasks.quality;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.tasks.code.GitCheckout;
import com.squarepolka.readyci.tasks.readyci.TaskCommand;
import org.springframework.stereotype.Component;

@Component
public class SonarqubeRunner extends Task {

    public static final String TASK_SONARQUBE_RUNNER = "sonarqube_runner";
    public static final String BUILD_PROP_SONAR_HOST_URL = "sonarHostUrl";
    public static final String BUILD_PROP_SONAR_LOGIN_KEY = "sonarLoginKey";
    public static final String BUILD_PROP_SONAR_PROJECT_KEY = "sonarProjectKey";
    public static final String BUILD_PROP_SONAR_SOURCE_PATH = "sonarSourcePath";
    public static final String BUILD_PROP_SONAR_BINARY_PATH = "sonarBinaryPath";
    public static final String BUILD_PROP_SONAR_BYPASS_BUILD_WRAPPER = "sonarBypassBuildWrapper";
    public static final String BUILD_PROP_SONAR_SUBMIT_BRANCH = "sonarSubmitBranch";
    public static final String BUILD_PROP_SONAR_TEST_PATH = "sonarTestsPath";
    public static final String BUILD_PROP_SONAR_JAVA_COVERAGE_PLUGIN = "sonarJavaCoveragePlugin";
    public static final String BUILD_PROP_SONAR_JACOCO_REPORT_PATH = "sonarJacocoReportPath";

    @Override
    public String taskIdentifier() {
        return TASK_SONARQUBE_RUNNER;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        TaskCommand taskCommand = new TaskCommand(buildEnvironment);
        taskCommand.addStringCommand("sonar-scanner").
        addEnvironmentParameter("-Dsonar.host.url", BUILD_PROP_SONAR_HOST_URL).
        addEnvironmentParameter("-Dsonar.login", BUILD_PROP_SONAR_LOGIN_KEY).
        addEnvironmentParameter("-Dsonar.projectKey", BUILD_PROP_SONAR_PROJECT_KEY).
        addEnvironmentParameter("-Dsonar.sources", BUILD_PROP_SONAR_SOURCE_PATH).
        addEnvironmentParameter("-Dsonar.java.binaries", BUILD_PROP_SONAR_BINARY_PATH).
        addEnvironmentParameter("-Dsonar.tests", BUILD_PROP_SONAR_TEST_PATH).
        addEnvironmentParameter("-Dsonar.java.coveragePlugin", BUILD_PROP_SONAR_JAVA_COVERAGE_PLUGIN).
        addEnvironmentParameter("-Dsonar.jacoco.reportPath", BUILD_PROP_SONAR_JACOCO_REPORT_PATH).
        addEnvironmentParameter("-Dsonar.cfamily.build-wrapper-output.bypass", BUILD_PROP_SONAR_BYPASS_BUILD_WRAPPER).
        addEnvironmentParameterIfConfiguredParamIsTrue("-Dsonar.branch.name", BUILD_PROP_SONAR_SUBMIT_BRANCH, GitCheckout.BUILD_PROP_GIT_BRANCH);
        executeCommand(taskCommand, buildEnvironment.getProjectPath());
    }

}

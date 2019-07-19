package com.squarepolka.readyci.tasks.code;

import com.squarepolka.readyci.exceptions.TaskExitException;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.readyci.TaskCommandHandler;
import com.squarepolka.readyci.util.PropertyMissingException;
import kotlin.collections.CollectionsKt;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.squarepolka.readyci.tasks.code.GitCheckout.BUILD_PROP_GIT_BRANCH;
import static com.squarepolka.readyci.tasks.code.GitCheckout.BUILD_PROP_GIT_PATH;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GitCheckoutTest {
    private static final String COMMAND_GIT = "/usr/bin/git";
    private static final String PARAM_CLONE = "clone";
    private static final String PARAM_FLAG_BRANCH = "-b";
    private static final String PARAM_BRANCH = "branch";
    private static final String PARAM_SINGLE_BRANCH = "--single-branch";
    private static final String PATH_CODE = "/code/";
    private static final String PATH_GIT = "git://path";
    private static final String PATH_TMP = "/tmp/";
    private static final String PATH_RUN_PATH = "/runpath/";
    private static final String BRANCH_GIT = "git_branch";
    private static final String TEST_BRANCH_VALUE = "feature/sonar_branch_awareness";
    private static final String TEST_BRANCH_OUTPUT = "develop\n" +
            "* feature/sonar_branch_awareness\n" +
            "fix/cleanup\n" +
            "help\n" +
            "master";

    private static final List<String> COMMAND_LATEST_COMMIT_MESSAGE =
            CollectionsKt.listOf(COMMAND_GIT, "log", "-1", "--pretty=%B");

    @InjectMocks
    private GitCheckout subject;
    @Mock
    private BuildEnvironment buildEnvironment;
    @Mock
    private TaskCommandHandler taskCommandHandler;

    @Test
    public void checkTaskIdentifier() {
        String taskName = subject.taskIdentifier();
        assertEquals("The GitCheckout task identifier is correct", "checkout_git", taskName);
    }

    @Test
    public void testGitCheckoutWithConfiguredGitPathAndBranchName() throws TaskExitException {
        // Arrange
        List<String> expectedCommand = CollectionsKt.listOf(COMMAND_GIT, PARAM_CLONE, PARAM_FLAG_BRANCH,
                BRANCH_GIT, PATH_GIT, PATH_CODE);
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_PATH)).thenReturn(PATH_GIT);
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH)).thenReturn(BRANCH_GIT);
        Mockito.when(buildEnvironment.getCodePath()).thenReturn(PATH_CODE);

        Mockito.when(taskCommandHandler.executeCommand(COMMAND_LATEST_COMMIT_MESSAGE, PATH_CODE))
                .thenReturn(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));

        // Act
        subject.performTask(buildEnvironment);

        // Assert
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, PATH_TMP);
        Mockito.verify(buildEnvironment, Mockito.times(1)).addProperty(BUILD_PROP_GIT_BRANCH, BRANCH_GIT);
    }

    @Test
    public void testGitCheckoutWithConfiguredGitPathAndNoBranchName() throws TaskExitException {
        // Arrange
        List<String> expectedCommand = CollectionsKt.listOf(COMMAND_GIT, PARAM_CLONE, PARAM_SINGLE_BRANCH, PATH_GIT, PATH_CODE);
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_PATH)).thenReturn(PATH_GIT);
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH)).thenThrow(new PropertyMissingException(BUILD_PROP_GIT_BRANCH));
        setupFakeGitCheckout();

        // Act
        subject.performTask(buildEnvironment);

        // Assert
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, PATH_TMP);
        Mockito.verify(buildEnvironment, Mockito.times(1)).addProperty(BUILD_PROP_GIT_BRANCH, TEST_BRANCH_VALUE);
    }

    @Test
    public void testGitCheckoutWithNoGitPathAndNoBranchName() throws TaskExitException {
        // Arrange
        List<String> expectedCommand = CollectionsKt.listOf(COMMAND_GIT, PARAM_BRANCH);
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_PATH)).thenThrow(new PropertyMissingException(BUILD_PROP_GIT_BRANCH));
        Mockito.when(buildEnvironment.getProperty(BUILD_PROP_GIT_BRANCH)).thenThrow(new PropertyMissingException(BUILD_PROP_GIT_BRANCH));

        Mockito.when(taskCommandHandler.executeCommand(COMMAND_LATEST_COMMIT_MESSAGE, PATH_CODE))
                .thenReturn(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));

        setupFakeGitCheckout();

        // Act
        subject.performTask(buildEnvironment);

        // Assert
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, PATH_CODE);
        Mockito.verify(buildEnvironment, Mockito.times(1)).addProperty(BUILD_PROP_GIT_BRANCH, TEST_BRANCH_VALUE);
    }

    private void setupFakeGitCheckout() {
        List<String> expectedCommand = CollectionsKt.listOf(COMMAND_GIT, PARAM_BRANCH);
        InputStream fakeInputStream = new ByteArrayInputStream(TEST_BRANCH_OUTPUT.getBytes(StandardCharsets.UTF_8));
        Mockito.when(taskCommandHandler.executeCommand(expectedCommand, PATH_CODE)).thenReturn(fakeInputStream);
        Mockito.when(buildEnvironment.getCodePath()).thenReturn(PATH_CODE);
        Mockito.when(buildEnvironment.getRealCIRunPath()).thenReturn(PATH_RUN_PATH);
    }
}
package com.squarepolka.readyci.tasks.build;

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

@RunWith(MockitoJUnitRunner.class)
public class BuildFolderCleanTest {

    @InjectMocks
    private BuildFolderClean subject;
    @Mock
    private BuildEnvironment buildEnvironment;
    @Mock
    private TaskCommandHandler taskCommandHandler;

    @Test
    public void taskRemovesSpecifiedFolder() {
        List<String> expectedCommand = new ArrayList<>();
        expectedCommand.add("rm");
        expectedCommand.add("-fR");
        expectedCommand.add("/tmp/readyci/random-uuid");
        String expectedWorkingDir = "/tmp/";
        Mockito.when(buildEnvironment.getScratchPath()).thenReturn("/tmp/readyci/random-uuid");
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(1)).executeCommand(expectedCommand, expectedWorkingDir);
    }

    @Test
    public void deleteMustBeWithinScratchFolder() {
        Mockito.when(buildEnvironment.getScratchPath()).thenReturn("/tmp/");
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(0)).executeCommand(Mockito.any(List.class), Mockito.anyString());
    }

    @Test
    public void scratchPathMustNotBeEmpty() {
        Mockito.when(buildEnvironment.getScratchPath()).thenReturn("");
        subject.performTask(buildEnvironment);
        Mockito.verify(taskCommandHandler, Mockito.times(0)).executeCommand(Mockito.any(List.class), Mockito.anyString());
    }

}

package com.squarepolka.readyci.tasks.build;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskFailedException;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class DeployCopy extends Task {

    public static final String TASK_DEPLOY_COPY = "deploy_copy";
    public static final String TASK_DEPLOY_SOURCE_PATH = "deploySrcPath";
    public static final String TASK_DEPLOY_DESTINATION_PATH = "deployDstPath";

    @Override
    public String taskIdentifier() {
        return TASK_DEPLOY_COPY;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws TaskFailedException {
        String sourcePath = buildEnvironment.getProperty(TASK_DEPLOY_SOURCE_PATH);
        String destinationPath = buildEnvironment.getProperty(TASK_DEPLOY_DESTINATION_PATH);

        executeCommand(new String[] {"/bin/cp", sourcePath, destinationPath}, buildEnvironment.getProjectPath());
    }
}

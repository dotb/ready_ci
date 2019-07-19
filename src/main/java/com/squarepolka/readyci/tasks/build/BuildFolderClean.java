package com.squarepolka.readyci.tasks.build;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class BuildFolderClean extends Task {

    public static final String TASK_BUILD_PATH_CLEAN = "build_path_clean";

    @Override
    public String taskIdentifier() {
        return TASK_BUILD_PATH_CLEAN;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        String buildPath = buildEnvironment.getScratchPath();

        if (buildPath.length() > 12 && buildPath.startsWith("/tmp/readyci")) {
            executeCommand(new String[] {"rm", "-fR", buildPath});
        }
    }
}

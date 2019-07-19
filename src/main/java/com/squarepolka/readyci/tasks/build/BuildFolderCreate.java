package com.squarepolka.readyci.tasks.build;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class BuildFolderCreate extends Task {

    public static final String TASK_BUILD_FOLDER_CREATE = "build_path_create";

    @Override
    public String taskIdentifier() {
        return TASK_BUILD_FOLDER_CREATE;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        String buildPath = buildEnvironment.getScratchPath();

        StringBuilder pathStrBuilder = new StringBuilder();
        String[] pathFolders = buildPath.split("/");
        for (String folder : pathFolders) {
            pathStrBuilder.append("/");
            pathStrBuilder.append(folder);

            if (!folderExists(pathStrBuilder.toString())) {
                createFolder(pathStrBuilder.toString());
            }
        }
    }

    public boolean folderExists(String path) {
        File folder = new File(path);
        return folder.exists();
    }

    public void createFolder(String path) {
        executeCommand(new String[] {"/bin/mkdir", path});
    }
}

package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskFailedException;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class AndroidCreateApkFile extends Task {

    public static final String TASK_CREATE_APK_FILE = "android_create_apk_file";
    public static final String BUILD_PROP_SCHEME = "scheme";


    @Override
    public String taskIdentifier() {
        return TASK_CREATE_APK_FILE;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws TaskFailedException {
        String scheme = buildEnvironment.getProperty(BUILD_PROP_SCHEME);
        String arg = String.format("assemble%s", scheme);
        executeCommand(new String[] {
                "./gradlew",
                arg
        }, buildEnvironment.getProjectPath());
    }
}

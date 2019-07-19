package com.squarepolka.readyci.tasks.app.android;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskFailedException;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import static com.squarepolka.readyci.configuration.AndroidPropConstants.BUILD_PROP_SCHEME;

@Component
public class AndroidRunUnitTest extends Task {

    public static final String TASK_ANDROID_UNIT_TEST = "android_unit_test";

    @Override
    public String taskIdentifier() {
        return TASK_ANDROID_UNIT_TEST;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws TaskFailedException {

        String scheme = buildEnvironment.getProperty(BUILD_PROP_SCHEME);
        scheme = scheme.substring(0, 1).toUpperCase() + scheme.substring(1);
        String arg = String.format("test%sUnitTest", scheme);
        executeCommand(new String[] {
                "./gradlew",
                arg
        }, buildEnvironment.getProjectPath());

    }
}

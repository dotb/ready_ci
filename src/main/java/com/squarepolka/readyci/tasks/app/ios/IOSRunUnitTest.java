package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskFailedException;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import static com.squarepolka.readyci.tasks.app.ios.IOSBuildArchive.BUILD_PROP_IOS_SCHEME;

@Component
public class IOSRunUnitTest  extends Task {

    public static final String TASK_UNIT_TEST = "ios_unit_test";

    public static final String BUILD_PROP_IOS_DESTINATION = "iosTestDestination";

    @Override
    public String taskIdentifier() {
        return TASK_UNIT_TEST;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws TaskFailedException {

        String workspace = String.format("%s.xcworkspace", buildEnvironment.getProperty("workspace"));
        String scheme = buildEnvironment.getProperty(BUILD_PROP_IOS_SCHEME);
        String destination = buildEnvironment.getProperty(BUILD_PROP_IOS_DESTINATION);

        executeCommand(new String[] {"/usr/bin/xcodebuild",
                "test",
                "-workspace", workspace,
                "-scheme", scheme,
                "-destination", destination,
                }, buildEnvironment.getProjectPath());

    }
}

package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.util.PropertyMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IOSDependenciesPods extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOSDependenciesPods.class);
    public static final String TASK_IOS_POD_INSTALL = "ios_pod_install";
    public static final String BUILD_PROP_POD_REPO_UPDATE = "iosPodUpdateRepo";

    @Override
    public String taskIdentifier() {
        return TASK_IOS_POD_INSTALL;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
        updatePodRepo(buildEnvironment);
        installPods(buildEnvironment);
    }

    private void updatePodRepo(BuildEnvironment buildEnvironment) {
        try {
            boolean shouldUpdateRepo = buildEnvironment.getSwitch(BUILD_PROP_POD_REPO_UPDATE);
            if (shouldUpdateRepo) {
                LOGGER.debug("The {} parameter was specified. Updating the Cocoapods repo", BUILD_PROP_POD_REPO_UPDATE);
                executeCommand(new String[]{"pod", "repo", "update"}, buildEnvironment.getProjectPath());
            }
        } catch (PropertyMissingException e) {
            LOGGER.debug("The {} parameter was not specified. Not updating the Cocoapods repo", BUILD_PROP_POD_REPO_UPDATE);
        }
    }

    private void installPods(BuildEnvironment buildEnvironment) {
        executeCommand(new String[]{"pod", "install"}, buildEnvironment.getProjectPath());
    }
}

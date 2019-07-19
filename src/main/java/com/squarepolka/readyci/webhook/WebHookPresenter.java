package com.squarepolka.readyci.webhook;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import com.squarepolka.readyci.taskrunner.TaskRunnerFactory;
import com.squarepolka.readyci.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class WebHookPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookPresenter.class);

    private TaskRunnerFactory taskRunnerFactory;

    @Autowired
    public WebHookPresenter(TaskRunnerFactory taskRunnerFactory) {
        this.taskRunnerFactory = taskRunnerFactory;
    }

    @Async
    public void handleWebHook(Map<String, Object> webHookRequest) {

        // Handle requests from BitBucket and GitHub. Starting with BitBucket
        String repository = Util.getMappedValueAtPath(webHookRequest, "repository.name");
        String branchName = Util.getMappedValueAtPath(webHookRequest, "push.changes.new.name");
        String gitAuthor = Util.getMappedValueAtPath(webHookRequest, "push.changes.new.target.author.raw");
        String commitMessage = Util.getMappedValueAtPath(webHookRequest, "push.changes.new.target.summary.raw");

        // Try load GitHub values if they are missing
        if (!Util.valueExists(branchName)) {
            branchName = Util.getMappedValueAtPath(webHookRequest, "ref");
        }
        if (!Util.valueExists(gitAuthor)) {
            gitAuthor = Util.getMappedValueAtPath(webHookRequest, "sender.login");
        }
        if (!Util.valueExists(commitMessage)) {
            commitMessage = Util.getMappedValueAtPath(webHookRequest, "head_commit.message");
        }

        if (validateGitCommit(repository, branchName, gitAuthor, commitMessage)) {
            LOGGER.info("Webhook received for repository {} and branch {} by user {}", repository, branchName, gitAuthor);
            handleBuildRequest(repository, branchName);
        } else {
            LOGGER.warn("Webhook ignored a request which didn't contain a branch");
        }
    }

    private void handleBuildRequest(String repository, String branch) {
        ReadyCIConfiguration configuration = ReadyCIConfiguration.instance();
        List<PipelineConfiguration> pipelineConfigurations = configuration.getPipelines(repository, branch);

        if (pipelineConfigurations.size() > 0) {
            LOGGER.info("Proceeding with build for {} matched pipelines", pipelineConfigurations.size());
            runPipelines(pipelineConfigurations);
        } else {
            LOGGER.warn("Ignoring build request for repository {} branch {}. No matching pipelines configured.", repository, branch);
        }
    }

    private void runPipelines(List<PipelineConfiguration> pipelineConfigurations) {
        for (PipelineConfiguration pipelineConfiguration : pipelineConfigurations) {
            TaskRunner taskRunner = taskRunnerFactory.createTaskRunner(pipelineConfiguration);
            taskRunner.runAllTasks();
        }
    }

    private boolean validateGitCommit(String repository, String branchName, String gitAuthor, String commitMessage) {
        if (Util.valueExists(repository) && Util.valueExists(branchName) && Util.valueExists(gitAuthor) && Util.valueExists(commitMessage)) {
            String instanceName = ReadyCIConfiguration.instance().getInstanceName();
            if (commitMessage.toLowerCase().contains(instanceName.toLowerCase())) {
                LOGGER.warn("Hmmm, I recognise this GIT commit on {} for branch {} by {}, " +
                        "because my name is in the commit message. I'm going to ignore this " +
                        "commit to avoid cyclic builds triggered through the web-hook. " +
                        "The commit message is {}.", repository, branchName, gitAuthor, commitMessage);
            } else {
                return true;
            }
        }
        return false;
    }

}

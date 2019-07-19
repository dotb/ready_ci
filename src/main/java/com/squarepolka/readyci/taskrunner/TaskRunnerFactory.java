package com.squarepolka.readyci.taskrunner;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.configuration.TaskConfiguration;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.tasks.code.GitCheckout;
import com.squarepolka.readyci.tasks.readyci.ConfigurationLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskRunnerFactory {

    private List<Task> allTasks;

    @Autowired
    public TaskRunnerFactory(List<Task> allTasks) {
        this.allTasks = allTasks;
    }

    public TaskRunner createTaskRunner(PipelineConfiguration pipelineConf) {
        BuildEnvironment buildEnvironment = new BuildEnvironment(pipelineConf);
        TaskRunner taskRunner = new TaskRunner(buildEnvironment, this);
        addDefaultTasks(taskRunner);
        List<Task> configuredTasks = createTaskListFromConfig(pipelineConf.getTasks());
        taskRunner.setConfiguredTasks(configuredTasks);
        return taskRunner;
    }

    public List<Task> createTaskListFromConfig(List<TaskConfiguration> taskConfigurations) {
        List<Task> taskList = new ArrayList<Task>();
        for (TaskConfiguration taskConfiguration : taskConfigurations) {
            Task task = findTaskForIdentifier(taskConfiguration.getTask());
            taskList.add(task);
        }
        return taskList;
    }

    private void addDefaultTasks(TaskRunner taskRunner) {
        taskRunner.addDefaultPreTask(findTaskForIdentifier("build_path_create"));
        taskRunner.addDefaultPreTask(findTaskForIdentifier(GitCheckout.TASK_CHECKOUT_GIT));
        taskRunner.addDefaultPreTask(findTaskForIdentifier(ConfigurationLoad.TASK_CONFIGURATION_LOAD));
        taskRunner.addDefaultPostTask(findTaskForIdentifier("build_path_clean"));
    }

    private Task findTaskForIdentifier(String taskIdentifer) {
        for (Task task : allTasks) {
            if (task.taskIdentifier().equalsIgnoreCase(taskIdentifer)) {
                return task;
            }
        }
        throw new TaskNotFoundException(taskIdentifer);
    }

}

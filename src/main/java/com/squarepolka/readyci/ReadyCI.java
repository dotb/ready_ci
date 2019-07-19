package com.squarepolka.readyci;

import com.squarepolka.readyci.configuration.PipelineConfiguration;
import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import com.squarepolka.readyci.taskrunner.TaskRunnerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static java.lang.System.exit;


@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan({"com.squarepolka.readyci", "tasks"})
public class ReadyCI implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyCI.class);
    private TaskRunnerFactory taskRunnerFactory;

    @Autowired
    public ReadyCI(TaskRunnerFactory taskRunnerFactory) {
        this.taskRunnerFactory = taskRunnerFactory;
    }

    public static void main(String[] arguments) {

        ReadyCIConfiguration readyCIConfiguration = ReadyCIConfiguration.instance();
        readyCIConfiguration.handleInputArguments(arguments);

        SpringApplication springApplication =
                new SpringApplicationBuilder()
                        .sources(ReadyCI.class)
                        .web(readyCIConfiguration.isServerMode())
                        .build();
        springApplication.run(arguments);
    }

    @Override
    public void run(String... args) throws Exception {
        ReadyCIConfiguration readyCIConfiguration = ReadyCIConfiguration.instance();
        if (readyCIConfiguration.isServerMode()) {
            LOGGER.info("Ready CI is in server mode");
        } else {
            LOGGER.info("Ready CI is in command-line mode");
            runCommandLinePipeline();
            exit(0);
        }
    }

    private void runCommandLinePipeline() {
        ReadyCIConfiguration readyCIConfiguration = ReadyCIConfiguration.instance();
        PipelineConfiguration pipelineConfiguration = readyCIConfiguration.getPipelineToRun();

        if (null != pipelineConfiguration) {
            LOGGER.info("Building pipeline {}", pipelineConfiguration.getName());
            TaskRunner taskRunner = taskRunnerFactory.createTaskRunner(pipelineConfiguration);
            taskRunner.runAllTasks();
        } else {
            LOGGER.error("I tried to run a command-line build but a pipeline name was not specified. Please specify a pipeline name, for example, using the argument pipeline=ready-ci");
        }
    }
}

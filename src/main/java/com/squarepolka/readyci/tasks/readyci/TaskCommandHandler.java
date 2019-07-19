package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

/**
 * Handles the execution of commands
 */
@Component
public class TaskCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCommandHandler.class);
    @Autowired
    private TaskProxyConfiguration taskProxyConfiguration;
    @Autowired
    private TaskOutputHandler taskOutputHandler;

    public InputStream executeCommand(List<String> command, String workingDirectory) {
        LOGGER.debug("Executing command: {}", Util.arrayToString(command));
        try {
            File workingDirectoryFile = new File(workingDirectory);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            ReadyCIConfiguration configuration = ReadyCIConfiguration.instance();
            processBuilder.directory(workingDirectoryFile);
            processBuilder.redirectErrorStream(true);
            taskProxyConfiguration.configureProxyServer(processBuilder, configuration);
            Process process = processBuilder.start();
            InputStream processInputStream = process.getInputStream();
            int sizeOfFiveMeg = 5000000;
            processInputStream.mark(sizeOfFiveMeg);
            taskOutputHandler.handleProcessOutput(process);
            checkProcessSuccess(process);
            taskOutputHandler.resetInputStream(processInputStream);
            return processInputStream;
        } catch (Exception e) {
            TaskExecuteException taskExecuteException = new TaskExecuteException(String.format("Exception %s. Tried to run command %s", e.toString(), Util.arrayToString(command)));
            taskExecuteException.setStackTrace(e.getStackTrace());
            throw taskExecuteException;
        }
    }

    private void checkProcessSuccess(Process process) {
        int exitValue = process.exitValue();
        if (exitValue != 0) {
            InputStream processInputStream = process.getInputStream();
            taskOutputHandler.resetInputStream(processInputStream);
            taskOutputHandler.printProcessOutput(processInputStream);
            throw new TaskExecuteException(String.format("Exited with %d", exitValue));
        }
    }

}
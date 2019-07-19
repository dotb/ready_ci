package com.squarepolka.readyci.tasks.readyci;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskConsolePrinter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskConsolePrinter.class);

    public void consolePrintln(String line) {
        LOGGER.trace(line);
    }

}

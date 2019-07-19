package com.squarepolka.readyci.beans;

import com.squarepolka.readyci.tasks.readyci.TaskOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public Logger taskOutputHandlerLogger() {
        return LoggerFactory.getLogger(TaskOutputHandler.class);
    }

}

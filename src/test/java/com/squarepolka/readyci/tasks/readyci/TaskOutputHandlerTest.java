package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class TaskOutputHandlerTest {

    @InjectMocks
    private TaskOutputHandler subject;

    @Mock
    Process process;

    @Mock
    InputStream inputStream;

    @Mock
    Logger taskOutputHandlerLogger;

    @Mock
    TaskConsolePrinter taskConsolePrinter;

    @Mock
    Util util;

    @Test
    public void handleProcessOutputChecksProcessIsAlive() {
        Mockito.when(process.isAlive()).thenReturn(true, false);
        Mockito.when(process.getInputStream()).thenReturn(inputStream);
        try {
            subject.handleProcessOutput(process);
            Mockito.verify(process, Mockito.times(1)).getInputStream();
        } catch (IOException e) { }
    }

    @Test
    public void handleProcessOutputChecksDebugEnabled() throws IOException {
        Mockito.when(process.isAlive()).thenReturn(true, false);
        Mockito.when(process.getInputStream()).thenReturn(inputStream);
        Mockito.when(taskOutputHandlerLogger.isDebugEnabled()).thenReturn(true);
        subject.handleProcessOutput(process);
        Mockito.verify(util, Mockito.times(0)).skipHalfOfStream(Mockito.any(InputStream.class));
    }

    @Test
    public void handleProcessOutputChecksDebugDisabled() throws IOException {
        Mockito.when(process.isAlive()).thenReturn(true, false);
        Mockito.when(process.getInputStream()).thenReturn(inputStream);
        Mockito.when(taskOutputHandlerLogger.isDebugEnabled()).thenReturn(false);
        subject.handleProcessOutput(process);
        Mockito.verify(util, Mockito.times(1)).skipHalfOfStream(Mockito.any(InputStream.class));
    }

    @Test
    public void testResetInputStream() throws IOException {
        subject.resetInputStream(inputStream);
        Mockito.verify(inputStream, Mockito.times(1)).reset();
    }

    @Test
    public void testPrintProcessOutput() {
        String testString = "Hello World\n Hello ReadyCI"; // Two lines of console output
        InputStream fakeInputStream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
        subject.printProcessOutput(fakeInputStream);
        Mockito.verify(taskConsolePrinter, Mockito.times(2)).consolePrintln(Mockito.anyString());
    }

}
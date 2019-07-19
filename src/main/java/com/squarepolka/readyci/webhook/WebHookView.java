package com.squarepolka.readyci.webhook;

import com.squarepolka.readyci.taskrunner.TaskRunnerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WebHookView {

    private TaskRunnerFactory taskRunnerFactory;
    private WebHookPresenter webHookPresenter;

    @Autowired
    public WebHookView(WebHookPresenter webHookPresenter, TaskRunnerFactory taskRunnerFactory) {
        this.webHookPresenter = webHookPresenter;
        this.taskRunnerFactory = taskRunnerFactory;
    }

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public WebHookResponse handleWebHook(@RequestBody Map<String, Object> webHookRequest) {

        webHookPresenter.handleWebHook(webHookRequest);

        WebHookResponse webHookResponse = new WebHookResponse();
        webHookResponse.setMessage("Thanks");

        return webHookResponse;
    }
}

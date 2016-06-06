package com.suning.csp.adapter.snfnc;


import com.gs.csp.framework.processor.Processable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/manage")
public class ManagerController {

    @Resource(name = "concurrentMap")
    ConcurrentHashMap<String, String> concurrentMap;

    @RequestMapping("/start")
    public String start() {
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        Map<String, Processable> mapProcessor = context.getBeansOfType(Processable.class);
        for (String key : mapProcessor.keySet()) {
            mapProcessor.get(key).start();
        }
        return "index";
    }

     @RequestMapping("/stop")
    public String stop() throws IOException {
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        Map<String, Processable> mapProcessor = context.getBeansOfType(Processable.class);
        for (String key : mapProcessor.keySet()) {
            mapProcessor.get(key).stop();
        }
        return "monitor";
    }

    @RequestMapping("/monitor")
    @ResponseBody
    public String monitor() throws IOException {
        return String.valueOf(concurrentMap.size());
    }
}

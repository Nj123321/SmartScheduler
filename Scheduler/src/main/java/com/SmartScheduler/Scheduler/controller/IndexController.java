package com.SmartScheduler.Scheduler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    /**
     * Future Frontend
     * @return
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}

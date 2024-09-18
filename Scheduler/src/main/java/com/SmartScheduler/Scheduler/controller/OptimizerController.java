package com.SmartScheduler.Scheduler.controller;

import com.SmartScheduler.Scheduler.dto.CourseRequest;
import com.SmartScheduler.Scheduler.service.OptimizerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/optimize")
@RequiredArgsConstructor
public class OptimizerController {
    private final OptimizerService scheduleService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void getSchedule(@RequestParam Integer uid) throws JsonProcessingException {
        scheduleService.getOptimalSchedule(uid);
    }
}

package com.SmartScheduler.Scheduler.controller;

import com.SmartScheduler.Scheduler.service.OptimizerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/optimize")
@RequiredArgsConstructor
public class OptimizerController {
    private final OptimizerService scheduleService;

    /**
     * Gets Optimized schedule
     * @param uid userID
     * @return optimized schedule names
     * @throws JsonProcessingException
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<List<String>> getSchedule(@RequestParam Integer uid) throws JsonProcessingException {
        return scheduleService.getOptimalSchedule(uid);
    }
}

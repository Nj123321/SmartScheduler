package com.SmartScheduler.Scheduler.controller;

import com.SmartScheduler.Scheduler.dto.ScheduleRequest;
import com.SmartScheduler.Scheduler.model.ScheduleDraft;
import com.SmartScheduler.Scheduler.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class ScheduleController {
    private final SchedulerService scheduleService;
    @GetMapping
    public List<ScheduleDraft.PlannedCourse> getSchedule(@RequestParam Integer uid){
        return scheduleService.getCoursePlan(uid);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addClass(@RequestBody ScheduleRequest buffRequest){
        System.out.println("hellooooo");
        System.out.println(buffRequest.getUid());
        scheduleService.addCourse(buffRequest);
    }
}

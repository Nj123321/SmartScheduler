package com.SmartScheduler.Scheduler.controller;

import com.SmartScheduler.Scheduler.dto.ScheduleRequest;
import com.SmartScheduler.Scheduler.model.ScheduleDraft;
import com.SmartScheduler.Scheduler.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
public class ScheduleController {
    private final SchedulerService scheduleService;

    /**
     * Gets planned courses with associated draft schedule
     * @param uid userid
     * @return
     */
    @GetMapping
    public List<ScheduleDraft.PlannedCourse> getSchedule(@RequestParam Integer uid){
        return scheduleService.getCoursePlan(uid);
    }

    /**
     * Updates Schedule draft with courses
     * @param buffRequest userdefined courses and semester/uid if applicable
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addClass(@RequestBody ScheduleRequest buffRequest){
        scheduleService.addCourse(buffRequest);
    }
}

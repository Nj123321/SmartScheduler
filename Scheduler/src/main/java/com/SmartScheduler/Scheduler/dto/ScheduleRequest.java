package com.SmartScheduler.Scheduler.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for ScheduleCourseRequirement DTO and includes user information
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequest {
    private List<ScheduleCourseRequirement> scheduleCourseRequirements = new ArrayList<>();
    private Integer uid;
    private Integer semesters;
}

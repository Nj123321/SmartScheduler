package com.SmartScheduler.Scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for schedule endpoint to fetch wanted course requirements from user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleCourseRequirement {
    private Integer cid;
    private String courseName;
    private Integer semesterRequirement = -1;
}

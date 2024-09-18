package com.SmartScheduler.Scheduler.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseNodes {
    String courseName;
    Integer difficulty;
    Integer semesterIndex = -1;
}

package com.SmartScheduler.Scheduler.service;

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

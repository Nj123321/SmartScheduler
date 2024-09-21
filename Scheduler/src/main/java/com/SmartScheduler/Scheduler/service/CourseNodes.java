package com.SmartScheduler.Scheduler.service;

import lombok.*;

/***
 * Currently Used to parse data
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseNodes {
    String courseName;
    Integer difficulty;
    Integer semesterIndex = -1;

    @Override
    public String toString() {
        return courseName;
    }
}

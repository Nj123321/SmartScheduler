package com.SmartScheduler.Scheduler.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * A single Draft Schedule for a Single User
 */
@Document(value = "CoursePlan")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ScheduleDraft {
    @Id
    private Integer uid;
    private List<PlannedCourse> plannedCourseList = new ArrayList<>();
    private Integer semesters;

    /**
     * Courses in the Schedule with semester Requirement for pinning
     */
    @Getter
    public static class PlannedCourse{
        Integer cid;
        String cname;
        Integer semesterRequirement = -1;
        public PlannedCourse(Integer cid, String cname, Integer semesterRequirement){
            this.cid = cid;
            this.cname = cname;
            this.semesterRequirement = semesterRequirement;
        }
    }
}

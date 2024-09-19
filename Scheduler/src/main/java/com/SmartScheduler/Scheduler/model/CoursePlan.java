package com.SmartScheduler.Scheduler.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(value = "CoursePlan")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CoursePlan {
    @Id
    private Integer uid;
    private List<PlannedCourse> plannedCourseList = new ArrayList<>();
    private Integer semesters;
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

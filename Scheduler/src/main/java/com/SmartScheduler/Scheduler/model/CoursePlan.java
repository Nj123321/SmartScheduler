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
    @Getter
    public static class PlannedCourse{
        Integer cid;
        String cname;
        Integer semester = -1;
        public PlannedCourse(Integer cid, String cname, Integer semester){
            this.cid = cid;
            this.cname = cname;
            this.semester = semester;
        }
    }
}

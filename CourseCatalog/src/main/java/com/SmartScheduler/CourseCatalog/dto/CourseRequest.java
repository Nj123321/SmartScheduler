package com.SmartScheduler.CourseCatalog.dto;

import com.SmartScheduler.CourseCatalog.model.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {
    private List<String> preReqs;
    private String courseName;
    private Integer difficulty;
    private Integer cid;
}

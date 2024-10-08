package com.SmartScheduler.CourseCatalog.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO wrapper for CourseRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseListRequest {
    private List<CourseRequest> courseRequestList;
}

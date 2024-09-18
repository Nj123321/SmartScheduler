package com.SmartScheduler.CourseCatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreRequisites {
    private String courseName;
    private Integer difficulty;
    private Integer parentIndex;
}

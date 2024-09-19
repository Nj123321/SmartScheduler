package com.SmartScheduler.Scheduler.dto;

import lombok.*;

/**
 * Prerequisites DTO for Optimizer endpoint to fetch courses from CourseCatalog
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PreRequisites {
    private String courseName;
    private Integer difficulty;
    private Integer parentIndex;
}

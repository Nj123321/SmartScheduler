package com.SmartScheduler.Scheduler.dto;

import lombok.*;

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

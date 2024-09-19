package com.SmartScheduler.Scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class buffNode {
    private Integer cid;
    private String courseName;
    private Integer semesterRequirement = -1;
    public buffNode(Integer cid, String cName){
        this.cid = cid;
        this.courseName = cName;
    }
}

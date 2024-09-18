package com.SmartScheduler.Scheduler.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class buffRequest {
    private List<buffNode> buffNodeList = new ArrayList<>();
    private Integer uid;
}

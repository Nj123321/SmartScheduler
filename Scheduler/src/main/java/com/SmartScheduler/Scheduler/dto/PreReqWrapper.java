package com.SmartScheduler.Scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wraps a list of PreRequisites DTOs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreReqWrapper {
    List<PreRequisites> preRequisitesList;
}

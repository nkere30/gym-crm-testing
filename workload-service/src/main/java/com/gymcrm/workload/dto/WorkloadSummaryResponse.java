package com.gymcrm.workload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkloadSummaryResponse {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;
    private List<YearSummary> years;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class YearSummary {
        private int year;
        private List<MonthSummary> months;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MonthSummary {
        private int month;
        private int totalMinutes;
    }
}


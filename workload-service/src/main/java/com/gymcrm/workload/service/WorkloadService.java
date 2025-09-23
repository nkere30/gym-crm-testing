package com.gymcrm.workload.service;

import com.gymcrm.workload.dto.MonthlyTotalResponse;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.dto.WorkloadEventRequest;

public interface WorkloadService {
    void recordEvent(WorkloadEventRequest request);
    MonthlyTotalResponse getMonthlyTotal(String trainerUsername, int year, int month);
    WorkloadSummaryResponse getTrainerSummary(String trainerUsername);
}

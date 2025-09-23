package com.gymcrm.workload.service;

import com.gymcrm.workload.dto.MonthlyTotalResponse;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.dto.WorkloadActionType;
import com.gymcrm.workload.dto.WorkloadEventRequest;
import com.gymcrm.workload.model.WorkloadEvent;
import com.gymcrm.workload.model.WorkloadSummary;
import com.gymcrm.workload.repository.WorkloadEventRepository;
import com.gymcrm.workload.repository.WorkloadSummaryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkloadServiceImpl implements WorkloadService {

    private final WorkloadEventRepository eventRepository;
    private final WorkloadSummaryRepository summaryRepository;

    @Override
    public void recordEvent(WorkloadEventRequest request) {
        WorkloadEvent event = WorkloadEvent.builder()
                .trainerUsername(request.getTrainerUsername())
                .trainerFirstName(request.getTrainerFirstName())
                .trainerLastName(request.getTrainerLastName())
                .isActive(request.getIsActive())
                .trainingDate(request.getTrainingDate())
                .trainingDuration(request.getTrainingDuration())
                .actionType(request.getActionType())
                .build();

        eventRepository.save(event);

        YearMonth ym = YearMonth.from(event.getTrainingDate());
        WorkloadSummary summary = summaryRepository
                .findByTrainerUsernameAndYearAndMonth(event.getTrainerUsername(), ym.getYear(), ym.getMonthValue())
                .orElseGet(() -> new WorkloadSummary(
                        null,
                        event.getTrainerUsername(),
                        event.getTrainerFirstName(),
                        event.getTrainerLastName(),
                        event.getIsActive(),
                        ym.getYear(),
                        ym.getMonthValue(),
                        0
                ));

        int adjustment = event.getActionType() == WorkloadActionType.ADD
                ? event.getTrainingDuration().intValue()
                : -event.getTrainingDuration().intValue();

        summary.setTotalMinutes(summary.getTotalMinutes() + adjustment);
        summaryRepository.save(summary);

        log.info("Recorded event and updated summary for {}: {}/{} now = {} min",
                event.getTrainerUsername(), ym.getYear(), ym.getMonthValue(), summary.getTotalMinutes());
    }

    @Override
    public MonthlyTotalResponse getMonthlyTotal(String trainerUsername, int year, int month) {
        return summaryRepository
                .findByTrainerUsernameAndYearAndMonth(trainerUsername, year, month)
                .map(summary -> new MonthlyTotalResponse(summary.getTotalMinutes()))
                .orElse(new MonthlyTotalResponse(0));
    }

    @Override
    public WorkloadSummaryResponse getTrainerSummary(String trainerUsername) {
        List<WorkloadSummary> summaries = summaryRepository.findByTrainerUsername(trainerUsername);

        if (summaries.isEmpty()) {
            throw new EntityNotFoundException("No workload found for trainer " + trainerUsername);
        }

        WorkloadSummary first = summaries.get(0);

        Map<Integer, List<WorkloadSummary>> byYear = summaries.stream()
                .collect(Collectors.groupingBy(WorkloadSummary::getYear));

        List<WorkloadSummaryResponse.YearSummary> years = byYear.entrySet().stream()
                .map(entry -> WorkloadSummaryResponse.YearSummary.builder()
                        .year(entry.getKey())
                        .months(entry.getValue().stream()
                                .map(s -> WorkloadSummaryResponse.MonthSummary.builder()
                                        .month(s.getMonth())
                                        .totalMinutes(s.getTotalMinutes())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return WorkloadSummaryResponse.builder()
                .trainerUsername(first.getTrainerUsername())
                .trainerFirstName(first.getTrainerFirstName())
                .trainerLastName(first.getTrainerLastName())
                .isActive(first.getIsActive())
                .years(years)
                .build();
    }

}

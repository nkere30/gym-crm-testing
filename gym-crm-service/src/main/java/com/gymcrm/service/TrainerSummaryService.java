package com.gymcrm.service;

import com.gymcrm.exception.TrainerSummaryException;
import com.gymcrm.model.TrainerSummary;
import com.gymcrm.repository.TrainerSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import com.gymcrm.model.TrainerSummary.MonthSummary;
import com.gymcrm.model.TrainerSummary.YearSummary;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerSummaryService {

    private final TrainerSummaryRepository repository;

    public TrainerSummary processEvent(String transactionId,
                                       String username,
                                       String firstName,
                                       String lastName,
                                       Boolean status,
                                       LocalDate trainingDate,
                                       Long duration) {
        if (!StringUtils.hasText(username) || trainingDate == null || duration <= 0) {
            log.error("transactionId={} - Invalid event data for username={}, date={}, duration={}",
                    transactionId, username, trainingDate, duration);
            throw new IllegalArgumentException("Invalid Event Data");
        }


        try {
            Optional<TrainerSummary> existing = repository.findByUsername(username);

            TrainerSummary trainerSummary = existing.orElseGet(() -> {
                TrainerSummary ts = new TrainerSummary();
                ts.setUsername(username);
                ts.setFirstName(firstName);
                ts.setLastName(lastName);
                ts.setStatus(status);
                ts.setYears(new ArrayList<>());
                return ts;
            });

            int yearValue = trainingDate.getYear();
            int monthValue = trainingDate.getMonthValue();

            YearSummary yearSummary = trainerSummary.getYears().stream()
                    .filter(y -> y.getYear() == yearValue)
                    .findFirst()
                    .orElseGet(() -> {
                        YearSummary ys = new YearSummary(yearValue, new ArrayList<>());
                        trainerSummary.getYears().add(ys);
                        return ys;
                    });

            MonthSummary monthSummary = yearSummary.getMonths().stream()
                    .filter(m -> m.getMonth() == monthValue)
                    .findFirst()
                    .orElseGet(() -> {
                        MonthSummary ms = new MonthSummary(monthValue, 0L);
                        yearSummary.getMonths().add(ms);
                        return ms;
                    });

            monthSummary.setTrainingsSummaryDuration(
                    (monthSummary.getTrainingsSummaryDuration() == null ? 0L : monthSummary.getTrainingsSummaryDuration()) + duration
            );

            log.info("transactionId={} - Processed training event for username={} (year={}, month={}, duration={})",
                    transactionId, username, yearValue, monthValue, duration);
            return repository.save(trainerSummary);
        } catch (Exception e) {
            log.error("transactionId={} - Failed to process training event for username={}",
                    transactionId, username, e);
            throw new TrainerSummaryException("Error processing trainer summary for username=" + username, e);
        }
    }
}

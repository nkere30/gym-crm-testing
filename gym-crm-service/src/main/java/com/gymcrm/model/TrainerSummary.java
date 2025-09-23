package com.gymcrm.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trainer_summaries")
@CompoundIndex(name = "idx_first_last_name", def = "{'firstName' : 1, 'lastName' : 1}")
public class TrainerSummary {

    @Id
    private String id;

    @NotBlank
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private Boolean status;

    @Valid
    @NotNull
    private List<YearSummary> years;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YearSummary {
        private int year;

        @NotNull
        private List<MonthSummary> months;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthSummary {
        private int month;

        @NotNull
        private Long trainingsSummaryDuration;
    }
}

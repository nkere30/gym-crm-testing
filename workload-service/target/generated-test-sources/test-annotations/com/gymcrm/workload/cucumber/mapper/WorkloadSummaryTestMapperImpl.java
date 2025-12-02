package com.gymcrm.workload.cucumber.mapper;

import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.dto.WorkloadSummaryResponse.MonthSummary;
import com.gymcrm.workload.dto.WorkloadSummaryResponse.YearSummary;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-03T00:18:30+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Microsoft)"
)
public class WorkloadSummaryTestMapperImpl implements WorkloadSummaryTestMapper {

    @Override
    public WorkloadSummaryResponse toResponse(Map<String, String> map) {
        if ( map == null ) {
            return null;
        }

        WorkloadSummaryResponse.WorkloadSummaryResponseBuilder workloadSummaryResponse = WorkloadSummaryResponse.builder();

        if ( map.containsKey( "trainerUsername" ) ) {
            workloadSummaryResponse.trainerUsername( map.get( "trainerUsername" ) );
        }
        if ( map.containsKey( "trainerFirstName" ) ) {
            workloadSummaryResponse.trainerFirstName( map.get( "trainerFirstName" ) );
        }
        if ( map.containsKey( "trainerLastName" ) ) {
            workloadSummaryResponse.trainerLastName( map.get( "trainerLastName" ) );
        }

        workloadSummaryResponse.isActive( Boolean.valueOf(map.get("isActive")) );
        workloadSummaryResponse.years( List.of(YearSummary.builder()
                .year(Integer.parseInt(map.get("year")))
                .months(List.of(MonthSummary.builder()
                        .month(Integer.parseInt(map.get("month")))
                        .totalMinutes(Integer.parseInt(map.get("totalMinutes")))
                        .build()))
                .build()) );

        return workloadSummaryResponse.build();
    }
}

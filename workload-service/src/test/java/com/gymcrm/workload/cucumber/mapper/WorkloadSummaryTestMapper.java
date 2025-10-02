package com.gymcrm.workload.cucumber.mapper;

import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.dto.WorkloadSummaryResponse.MonthSummary;
import com.gymcrm.workload.dto.WorkloadSummaryResponse.YearSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper(imports = {List.class, YearSummary.class, MonthSummary.class})
public interface WorkloadSummaryTestMapper {

    WorkloadSummaryTestMapper INSTANCE = Mappers.getMapper(WorkloadSummaryTestMapper.class);

    @Mapping(target = "trainerUsername", source = "map.trainerUsername")
    @Mapping(target = "trainerFirstName", source = "map.trainerFirstName")
    @Mapping(target = "trainerLastName", source = "map.trainerLastName")
    @Mapping(target = "isActive", expression = "java(Boolean.valueOf(map.get(\"isActive\")))")
    @Mapping(
            target = "years",
            expression = "java(List.of(YearSummary.builder()\n" +
                    "    .year(Integer.valueOf(map.get(\"year\")))\n" +
                    "    .months(List.of(MonthSummary.builder()\n" +
                    "        .month(Integer.valueOf(map.get(\"month\")))\n" +
                    "        .totalMinutes(Integer.valueOf(map.get(\"totalMinutes\")))\n" +
                    "        .build()))\n" +
                    "    .build()))")
    WorkloadSummaryResponse toResponse(Map<String, String> map);
}

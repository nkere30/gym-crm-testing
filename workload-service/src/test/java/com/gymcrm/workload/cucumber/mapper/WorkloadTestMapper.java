package com.gymcrm.workload.cucumber.mapper;

import com.gymcrm.workload.dto.WorkloadActionType;
import com.gymcrm.workload.dto.WorkloadEventRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface WorkloadTestMapper {
    WorkloadTestMapper INSTANCE = Mappers.getMapper(WorkloadTestMapper.class);

    @Mapping(target = "actionType", expression = "java(safeActionType(map.get(\"actionType\")))")
    WorkloadEventRequest toRequest(Map<String, String> map);

    default WorkloadActionType safeActionType(String actionType) {
        try {
            return actionType != null ? WorkloadActionType.valueOf(actionType) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

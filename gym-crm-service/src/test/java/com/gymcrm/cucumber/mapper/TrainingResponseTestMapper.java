package com.gymcrm.cucumber.mapper;

import com.gymcrm.dto.training.TrainingCreateRequest;
import com.gymcrm.dto.training.TrainingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TrainingResponseTestMapper {
    TrainingResponseTestMapper INSTANCE = Mappers.getMapper(TrainingResponseTestMapper.class);

    @Mapping(target = "id", constant = "1L")
    TrainingResponse toResponse(TrainingCreateRequest request);
}

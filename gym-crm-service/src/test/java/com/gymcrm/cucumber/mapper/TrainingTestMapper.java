package com.gymcrm.cucumber.mapper;

import com.gymcrm.dto.training.TrainingCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Map;

@Mapper(imports = {LocalDate.class})
public interface TrainingTestMapper {
    TrainingTestMapper INSTANCE = Mappers.getMapper(TrainingTestMapper.class);

    TrainingCreateRequest toRequest(Map<String, String> data);
}

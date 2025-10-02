package com.gymcrm.cucumber.mapper;

import com.gymcrm.dto.trainee.TraineeRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface TraineeTestMapper {
    TraineeTestMapper INSTANCE = Mappers.getMapper(TraineeTestMapper.class);

    TraineeRegistrationRequest toRequest(Map<String, String> data);
}

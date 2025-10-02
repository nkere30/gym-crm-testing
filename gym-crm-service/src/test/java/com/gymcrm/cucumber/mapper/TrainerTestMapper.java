package com.gymcrm.cucumber.mapper;

import com.gymcrm.dto.trainer.TrainerRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface TrainerTestMapper {
    TrainerTestMapper INSTANCE = Mappers.getMapper(TrainerTestMapper.class);

    TrainerRegistrationRequest toRequest(Map<String, String> data);
}

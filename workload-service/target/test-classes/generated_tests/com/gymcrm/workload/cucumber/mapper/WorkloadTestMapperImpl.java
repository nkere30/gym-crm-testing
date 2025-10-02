package com.gymcrm.workload.cucumber.mapper;

import com.gymcrm.workload.dto.WorkloadEventRequest;
import java.time.LocalDate;
import java.util.Map;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-02T09:40:46+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
public class WorkloadTestMapperImpl implements WorkloadTestMapper {

    @Override
    public WorkloadEventRequest toRequest(Map<String, String> map) {
        if ( map == null ) {
            return null;
        }

        WorkloadEventRequest workloadEventRequest = new WorkloadEventRequest();

        if ( map.containsKey( "trainerUsername" ) ) {
            workloadEventRequest.setTrainerUsername( map.get( "trainerUsername" ) );
        }
        if ( map.containsKey( "trainerFirstName" ) ) {
            workloadEventRequest.setTrainerFirstName( map.get( "trainerFirstName" ) );
        }
        if ( map.containsKey( "trainerLastName" ) ) {
            workloadEventRequest.setTrainerLastName( map.get( "trainerLastName" ) );
        }
        if ( map.containsKey( "isActive" ) ) {
            workloadEventRequest.setIsActive( Boolean.parseBoolean( map.get( "isActive" ) ) );
        }
        if ( map.containsKey( "trainingDate" ) ) {
            workloadEventRequest.setTrainingDate( LocalDate.parse( map.get( "trainingDate" ) ) );
        }
        if ( map.containsKey( "trainingDuration" ) ) {
            workloadEventRequest.setTrainingDuration( Long.parseLong( map.get( "trainingDuration" ) ) );
        }

        workloadEventRequest.setActionType( safeActionType(map.get("actionType")) );

        return workloadEventRequest;
    }
}

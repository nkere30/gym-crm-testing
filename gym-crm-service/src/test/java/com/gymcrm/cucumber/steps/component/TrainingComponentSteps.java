package com.gymcrm.cucumber.steps.component;

import com.gymcrm.controller.TrainingController;
import com.gymcrm.dto.WorkloadEventRequest;
import com.gymcrm.dto.training.TrainingCreateRequest;
import com.gymcrm.dto.training.TrainingResponse;
import com.gymcrm.facade.TrainingFacade;
import com.gymcrm.messaging.WorkloadEventsPublisher;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingComponentSteps {


    private final TrainingFacade trainingFacade = Mockito.mock(TrainingFacade.class);
    private final TrainingController trainingController = new TrainingController(trainingFacade);
    private final WorkloadEventsPublisher workloadEventsPublisher = Mockito.mock(WorkloadEventsPublisher.class);

    private TrainingCreateRequest trainingCreateRequest;
    private ResponseEntity<TrainingResponse> response;
    private Exception caughtException;

    @Given("a valid training creation request:")
    public void a_valid_training_creation_request(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeUsername(data.get("traineeUsername"));
        request.setTrainerUsername("trainerUsername");
        request.setTrainingName("trainingName");
        request.setTrainingDate(LocalDate.parse(data.get("trainingDate")));
        request.setTrainingDuration(Long.parseLong(data.get("trainingDuration")));
        this.trainingCreateRequest = request;
    }

    @When("I create the training")
    public void i_create_the_training() {
        try {
            Mockito.doNothing().when(trainingFacade).createTraining(Mockito.any(), Mockito.any());
            Principal principal = () -> trainingCreateRequest.getTraineeUsername();
            response = trainingController.addTraining(trainingCreateRequest, principal);
        } catch (Exception e) {
            caughtException = e;
        }
    }


    @Then("the training creation response status should be {int}")
    public void the_training_creation_response_status_should_be(Integer expectedStatus) {
        if (caughtException != null) {
            fail("Unexpected exception: " + caughtException.getMessage());
        }
        assertEquals(expectedStatus.intValue(), response.getStatusCode().value());
    }

    @Then("the response should contain the training details")
    public void the_response_should_contain_the_training_details() {
        if (caughtException != null) {
            fail("Unexpected exception: " + caughtException.getMessage());
        }

        TrainingResponse trainingResponse = response.getBody();
        assertNotNull(trainingResponse, "Response body should not be null");

        assertEquals(trainingCreateRequest.getTraineeUsername(), trainingResponse.getTraineeUsername());
        assertEquals(trainingCreateRequest.getTrainerUsername(), trainingResponse.getTrainerUsername());
        assertEquals(trainingCreateRequest.getTrainingName(), trainingResponse.getTrainingName());
        assertEquals(trainingCreateRequest.getTrainingDate(), trainingResponse.getTrainingDate());
        assertEquals(trainingCreateRequest.getTrainingDuration(), trainingResponse.getTrainingDuration());
    }


    @Then("a training created event should be published")
    public void a_training_created_event_should_be_published() {
        if (caughtException != null) {
            fail("Unexpected exception: " + caughtException.getMessage());
        }

        Mockito.verify(workloadEventsPublisher, Mockito.times(1))
                .publish(Mockito.any(WorkloadEventRequest.class));
    }


}

package com.gymcrm.cucumber.steps.component;

import com.gymcrm.controller.TrainingController;
import com.gymcrm.dto.WorkloadEventRequest;
import com.gymcrm.dto.training.TrainingCreateRequest;
import com.gymcrm.dto.training.TrainingResponse;
import com.gymcrm.facade.TrainingFacade;
import com.gymcrm.messaging.WorkloadEventsPublisher;
import com.gymcrm.cucumber.mapper.TrainingRequestTestMapper;
import com.gymcrm.cucumber.mapper.TrainingResponseTestMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingComponentSteps {

    // mocks
    private final TrainingFacade trainingFacade = Mockito.mock(TrainingFacade.class);
    private final TrainingController trainingController = new TrainingController(trainingFacade);
    private final WorkloadEventsPublisher workloadEventsPublisher = Mockito.mock(WorkloadEventsPublisher.class);

    // state
    private TrainingCreateRequest trainingCreateRequest;
    private ResponseEntity<TrainingResponse> creationResponse;
    private ResponseEntity<Void> deletionResponse;

    // to capture logs
    private ListAppender<ILoggingEvent> listAppender;

    // Validator for simulating @Valid without Spring context
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    // GIVEN
    @Given("a training creation request:")
    public void a_training_creation_request(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        this.trainingCreateRequest = TrainingRequestTestMapper.INSTANCE.toRequest(data);
    }

    @Given("an existing training with id {int}")
    public void an_existing_training_with_id(Integer trainingId) {
        Mockito.doNothing().when(trainingFacade).deleteTraining(trainingId.longValue());
    }

    @Given("no training exists with id {int}")
    public void no_training_exists_with_id(Integer trainingId) {
        Mockito.doThrow(new IllegalArgumentException("Training not found"))
                .when(trainingFacade).deleteTraining(trainingId.longValue());
    }

    // WHEN
    @When("I create the training")
    public void i_create_the_training() {
        attachLogAppender();

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(trainingCreateRequest);
        if (!violations.isEmpty()) {
            creationResponse = ResponseEntity.badRequest().build();
            return;
        }

        TrainingResponse mockResponse = TrainingResponseTestMapper.INSTANCE.toResponse(trainingCreateRequest);
        Mockito.when(trainingFacade.createTraining(Mockito.any(), Mockito.any()))
                .thenReturn(mockResponse);

        Principal principal = () -> trainingCreateRequest.getTraineeUsername();
        creationResponse = trainingController.addTraining(trainingCreateRequest, principal);
    }

    @When("I delete the training with id {int}")
    public void i_delete_the_training_with_id(Integer trainingId) {
        attachLogAppender();
        try {
            deletionResponse = trainingController.deleteTraining(trainingId.longValue());
        } catch (IllegalArgumentException e) {
            deletionResponse = ResponseEntity.notFound().build();
        }
    }

    // THEN

    @Then("the training creation should succeed with status {int}")
    public void the_training_creation_should_succeed_with_status(Integer expectedStatus) {
        assertNotNull(creationResponse, "Expected creation response to be present");
        assertEquals(expectedStatus.intValue(), creationResponse.getStatusCode().value());
    }

    @Then("the training creation should fail with status {int}")
    public void the_training_creation_should_fail_with_status(Integer expectedStatus) {
        assertNotNull(creationResponse, "Expected creation response to be present");
        assertEquals(expectedStatus.intValue(), creationResponse.getStatusCode().value());
    }

    @Then("the training deletion should succeed with status {int}")
    public void the_training_deletion_should_succeed_with_status(Integer expectedStatus) {
        assertNotNull(deletionResponse, "Expected deletion response to be present");
        assertEquals(expectedStatus.intValue(), deletionResponse.getStatusCode().value());
    }

    @Then("the training deletion should fail with status {int}")
    public void the_training_deletion_should_fail_with_status(Integer expectedStatus) {
        assertNotNull(deletionResponse, "Expected deletion response to be present");
        assertEquals(expectedStatus.intValue(), deletionResponse.getStatusCode().value());
    }

    @Then("the response should contain the training details")
    public void the_response_should_contain_the_training_details() {
        TrainingResponse trainingResponse = creationResponse.getBody();
        assertNotNull(trainingResponse, "Response body should not be null");
        assertNotNull(trainingResponse.getId(), "Training id should not be null");
        assertEquals(trainingCreateRequest.getTraineeUsername(), trainingResponse.getTraineeUsername());
        assertEquals(trainingCreateRequest.getTrainerUsername(), trainingResponse.getTrainerUsername());
        assertEquals(trainingCreateRequest.getTrainingName(), trainingResponse.getTrainingName());
        assertEquals(trainingCreateRequest.getTrainingDate(), trainingResponse.getTrainingDate());
        assertEquals(trainingCreateRequest.getTrainingDuration(), trainingResponse.getTrainingDuration());
    }

    @Then("a training created event should be published")
    public void a_training_created_event_should_be_published() {
        Mockito.verify(workloadEventsPublisher, Mockito.times(1))
                .publish(Mockito.any(WorkloadEventRequest.class));
    }

    @Then("the logs should contain a transactionId")
    public void the_logs_should_contain_a_transaction_id() {
        List<ILoggingEvent> logsList = listAppender.list;
        boolean containsTx = logsList.stream()
                .anyMatch(event -> event.getFormattedMessage()
                        .matches(".*\\[[0-9a-f\\-]{36}].*"));
        assertTrue(containsTx, "Expected logs to contain a transactionId in UUID format");
    }

    private void attachLogAppender() {
        Logger controllerLogger = (Logger) LoggerFactory.getLogger("com.gymcrm.controller.TrainingController");
        listAppender = new ListAppender<>();
        listAppender.start();
        controllerLogger.addAppender(listAppender);
    }
}

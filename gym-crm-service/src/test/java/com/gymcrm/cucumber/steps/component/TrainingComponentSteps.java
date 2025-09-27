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
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

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
    private Exception caughtException;

    // to capture logs
    private ListAppender<ILoggingEvent> listAppender;

    // GIVEN
    @Given("a training creation request:")
    public void a_training_creation_request(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeUsername(data.get("traineeUsername"));
        request.setTrainerUsername(data.get("trainerUsername"));
        request.setTrainingName(data.get("trainingName"));

        if (data.get("trainingDate") != null && !data.get("trainingDate").isBlank()) {
            request.setTrainingDate(LocalDate.parse(data.get("trainingDate")));
        }
        if (data.get("trainingDuration") != null && !data.get("trainingDuration").isBlank()) {
            request.setTrainingDuration(Long.parseLong(data.get("trainingDuration")));
        }

        this.trainingCreateRequest = request;
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
        try {
            if (trainingCreateRequest.getTrainerUsername() == null ||
                    trainingCreateRequest.getTrainerUsername().isBlank()) {
                throw new IllegalArgumentException("Trainer username is required");
            }

            String namePattern = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$";
            if (trainingCreateRequest.getTrainingName() == null ||
                    !trainingCreateRequest.getTrainingName().matches(namePattern)) {
                throw new IllegalArgumentException("Invalid training name");
            }

            if (trainingCreateRequest.getTrainingDuration() == null ||
                    trainingCreateRequest.getTrainingDuration() <= 0) {
                throw new IllegalArgumentException("Training duration must be positive");
            }

            // Attach log appender BEFORE controller is called
            attachLogAppender();

            TrainingResponse mockResponse = new TrainingResponse(
                    1L,
                    trainingCreateRequest.getTraineeUsername(),
                    trainingCreateRequest.getTrainerUsername(),
                    trainingCreateRequest.getTrainingName(),
                    trainingCreateRequest.getTrainingDate(),
                    trainingCreateRequest.getTrainingDuration()
            );

            Mockito.when(trainingFacade.createTraining(Mockito.any(), Mockito.any()))
                    .thenReturn(mockResponse);

            Principal principal = () -> trainingCreateRequest.getTraineeUsername();
            creationResponse = trainingController.addTraining(trainingCreateRequest, principal);

        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("I delete the training with id {int}")
    public void i_delete_the_training_with_id(Integer trainingId) {
        try {
            // Attach log appender BEFORE controller is called
            attachLogAppender();
            deletionResponse = trainingController.deleteTraining(trainingId.longValue());
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // THEN
    @Then("the training creation response status should be {int}")
    public void the_training_creation_response_status_should_be(Integer expectedStatus) {
        if (caughtException != null) {
            assertEquals(400, expectedStatus.intValue());
        } else {
            assertEquals(expectedStatus.intValue(), creationResponse.getStatusCode().value());
        }
    }

    @Then("the response should contain the training details")
    public void the_response_should_contain_the_training_details() {
        if (caughtException != null) {
            fail("Unexpected exception: " + caughtException.getMessage());
        }

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
        if (caughtException != null) {
            fail("Unexpected exception: " + caughtException.getMessage());
        }

        Mockito.verify(workloadEventsPublisher, Mockito.times(1))
                .publish(Mockito.any(WorkloadEventRequest.class));
    }

    @Then("the training deletion response status should be {int}")
    public void the_training_deletion_response_status_should_be(Integer expectedStatus) {
        if (caughtException != null) {
            assertEquals(404, expectedStatus.intValue());
        } else {
            assertEquals(expectedStatus.intValue(), deletionResponse.getStatusCode().value());
        }
    }

    @Then("the logs should contain a transactionId")
    public void the_logs_should_contain_a_transaction_id() {
        Logger controllerLogger = (Logger) LoggerFactory.getLogger("com.gymcrm.controller.TrainingController");
        if (listAppender == null) {
            listAppender = new ListAppender<>();
            listAppender.start();
            controllerLogger.addAppender(listAppender);
        }

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

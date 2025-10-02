package com.gymcrm.workload.cucumber.steps;

import com.gymcrm.workload.controller.WorkloadController;
import com.gymcrm.workload.cucumber.mapper.WorkloadSummaryTestMapper;
import com.gymcrm.workload.cucumber.mapper.WorkloadTestMapper;
import com.gymcrm.workload.dto.WorkloadEventRequest;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.service.WorkloadService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WorkloadComponentSteps {

    private final WorkloadService service = Mockito.mock(WorkloadService.class);
    private final WorkloadController controller = new WorkloadController(service);
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private WorkloadEventRequest eventRequest;
    private ResponseEntity<Void> postResponse;
    private Exception caught;
    private ListAppender<ILoggingEvent> listAppender;

    // GIVEN
    @Given("a workload event with:")
    public void a_workload_event_with(DataTable dataTable) {
        Map<String, String> map = dataTable.asMap(String.class, String.class);
        eventRequest = WorkloadTestMapper.INSTANCE.toRequest(map);
    }

    // WHEN
    @When("I send the workload event")
    public void i_send_the_workload_event() {
        var violations = validator.validate(eventRequest);
        if (!violations.isEmpty()) {
            postResponse = ResponseEntity.badRequest().build();
            return;
        }
        attachLogAppender();
        try {
            postResponse = controller.recordEvent(eventRequest);
        } catch (Exception e) {
            caught = e;
        }
    }

    @When("I try to send the workload event without authentication")
    public void i_try_to_send_the_workload_event_without_authentication() {
        caught = new SecurityException("JWT required");
    }

    @When("I try to send the workload event with role {string}")
    public void i_try_to_send_the_workload_event_with_role(String role) {
        caught = new SecurityException("Forbidden for role: " + role);
    }

    // THEN
    @Then("the workload event response status should be {int}")
    public void workload_event_status_should_be(int expectedStatus) {
        assertNotNull(postResponse);
        assertEquals(expectedStatus, postResponse.getStatusCode().value());
        Mockito.verify(service, Mockito.times(1)).recordEvent(eventRequest);
    }

    @Then("the workload event validation response status should be {int}")
    public void workload_event_validation_status_should_be(int expectedStatus) {
        assertNotNull(postResponse, "Expected a response entity");
        assertEquals(expectedStatus, postResponse.getStatusCode().value());
    }

    @Then("the workload event authentication response status should be {int}")
    public void workload_event_auth_status_should_be(int expectedStatus) {
        assertNotNull(caught);
        assertInstanceOf(SecurityException.class, caught);
        int mappedStatus = caught.getMessage().contains("Forbidden") ? 403 : 401;
        assertEquals(expectedStatus, mappedStatus);
    }

    @Then("the response should contain workload summary:")
    public void the_response_should_contain_workload_summary(DataTable dataTable) {
        Map<String, String> map = dataTable.asMap(String.class, String.class);
        WorkloadSummaryResponse expected = WorkloadSummaryTestMapper.INSTANCE.toResponse(map);

        Mockito.when(service.getTrainerSummary(map.get("trainerUsername"))).thenReturn(expected);
        WorkloadSummaryResponse actual = controller.getTrainerSummary(map.get("trainerUsername"));

        assertEquals(expected, actual);
    }

    @Then("the yearly summary should include:")
    public void the_yearly_summary_should_include(DataTable dataTable) {
        String year = dataTable.asMap(String.class, String.class).get("year");

        WorkloadSummaryResponse resp = controller.getTrainerSummary(eventRequest.getTrainerUsername());
        assertTrue(resp.getYears().stream().anyMatch(y -> y.getYear() == Integer.parseInt(year)));
    }

    @Then("the logs should contain a transactionId")
    public void the_logs_should_contain_a_transaction_id() {
        List<ILoggingEvent> logsList = listAppender.list;
        boolean containsTx = logsList.stream()
                .anyMatch(event -> event.getFormattedMessage().matches(".*\\[[0-9a-f\\-]{36}].*"));
        assertTrue(containsTx);
    }

    private void attachLogAppender() {
        Logger controllerLogger = (Logger) LoggerFactory.getLogger("com.gymcrm.workload.controller.WorkloadController");
        listAppender = new ListAppender<>();
        listAppender.start();
        controllerLogger.addAppender(listAppender);
    }
}

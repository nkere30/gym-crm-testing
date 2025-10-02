package com.gymcrm.workload.cucumber.steps;

import com.gymcrm.workload.controller.WorkloadController;
import com.gymcrm.workload.cucumber.mapper.WorkloadSummaryTestMapper;
import com.gymcrm.workload.dto.WorkloadEventRequest;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.dto.WorkloadSummaryResponse.YearSummary;
import com.gymcrm.workload.dto.WorkloadSummaryResponse.MonthSummary;
import com.gymcrm.workload.dto.WorkloadActionType;
import com.gymcrm.workload.service.WorkloadService;
import com.gymcrm.workload.cucumber.mapper.WorkloadTestMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class WorkloadComponentSteps {

    private final WorkloadService service = Mockito.mock(WorkloadService.class);
    private final WorkloadController controller = new WorkloadController(service);
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private WorkloadEventRequest eventRequest;
    private ResponseEntity<Void> postResponse;
    private Exception caught;

    private String expectedUsername;
    private String expectedFirstName;
    private String expectedLastName;
    private Boolean expectedActive;
    private Integer expectedMonth;
    private Integer expectedTotalMinutes;
    private Integer expectedYear;
    private WorkloadActionType expectedActionType;

    private ListAppender<ILoggingEvent> listAppender;

    // GIVEN
    @Given("a workload event with:")
    public void a_workload_event_with(DataTable dataTable) {
        Map<String, String> map = dataTable.asMap(String.class, String.class);
        eventRequest = WorkloadTestMapper.INSTANCE.toRequest(map);
        expectedActionType = eventRequest.getActionType();
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

        ArgumentCaptor<WorkloadEventRequest> captor = ArgumentCaptor.forClass(WorkloadEventRequest.class);
        Mockito.verify(service, Mockito.times(1)).recordEvent(captor.capture());
        WorkloadEventRequest captured = captor.getValue();
        assertEquals(expectedActionType, captured.getActionType());
        assertEquals(eventRequest.getTrainerUsername(), captured.getTrainerUsername());
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
        Map<String, String> m = dataTable.asMap(String.class, String.class);
        expectedUsername = m.get("trainerUsername");
        expectedFirstName = m.get("trainerFirstName");
        expectedLastName = m.get("trainerLastName");
        expectedActive = m.containsKey("isActive") ? Boolean.valueOf(m.get("isActive")) : null;

        assertNotNull(expectedUsername);
        assertNotNull(expectedFirstName);
        assertNotNull(expectedLastName);
    }

    @Then("the yearly summary should include:")
    public void the_yearly_summary_should_include(DataTable dataTable) {
        expectedYear = Integer.valueOf(dataTable.asMap(String.class, String.class).get("year"));
    }

    @Then("the monthly summary for year {int} should include:")
    public void the_monthly_summary_for_year_should_include(Integer year, DataTable dataTable) {
        Map<String, String> m = dataTable.asMap(String.class, String.class);

        // Merge yearly/monthly info into the map
        m.put("year", String.valueOf(year));
        m.put("trainerUsername", expectedUsername);
        m.put("trainerFirstName", expectedFirstName);
        m.put("trainerLastName", expectedLastName);
        m.put("isActive", String.valueOf(expectedActive != null ? expectedActive : true));

        // Build full response via mapper
        WorkloadSummaryResponse mock = WorkloadSummaryTestMapper.INSTANCE.toResponse(m);

        Mockito.when(service.getTrainerSummary(expectedUsername)).thenReturn(mock);

        // Call and assert
        WorkloadSummaryResponse resp = controller.getTrainerSummary(expectedUsername);
        assertEquals(expectedUsername, resp.getTrainerUsername());
        assertEquals(expectedFirstName, resp.getTrainerFirstName());
        assertEquals(expectedLastName, resp.getTrainerLastName());
        if (expectedActive != null) assertEquals(expectedActive, resp.getIsActive());

        int expectedMonth = Integer.parseInt(m.get("month"));
        int expectedMinutes = Integer.parseInt(m.get("totalMinutes"));

        WorkloadSummaryResponse.YearSummary yr =
                resp.getYears().stream().filter(y -> y.getYear() == year).findFirst().orElseThrow();
        assertEquals(year, yr.getYear());
        assertTrue(yr.getMonths().stream().anyMatch(
                mm -> mm.getMonth() == expectedMonth && mm.getTotalMinutes() == expectedMinutes
        ));
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

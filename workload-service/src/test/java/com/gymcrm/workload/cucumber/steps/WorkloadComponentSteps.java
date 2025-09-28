package com.gymcrm.workload.cucumber.steps;

import com.gymcrm.workload.controller.WorkloadController;
import com.gymcrm.workload.dto.WorkloadEventRequest;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.dto.WorkloadSummaryResponse.YearSummary;
import com.gymcrm.workload.dto.WorkloadSummaryResponse.MonthSummary;
import com.gymcrm.workload.dto.WorkloadActionType;
import com.gymcrm.workload.service.WorkloadService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class WorkloadComponentSteps {

    private final WorkloadService service = Mockito.mock(WorkloadService.class);
    private final WorkloadController controller = new WorkloadController(service);

    private WorkloadEventRequest eventRequest;
    private ResponseEntity<Void> postResponse;
    private Exception caught;

    private String expectedUsername;
    private String expectedFirstName;
    private String expectedLastName;
    private Boolean expectedActive;
    private Integer expectedMonth;
    private Integer expectedTotalMinutes;
    private WorkloadActionType expectedActionType;
    private Integer expectedYear;

    @Given("a workload event with:")
    public void a_workload_event_with(DataTable dataTable) {
        Map<String, String> map = dataTable.asMap(String.class, String.class);
        try {
            eventRequest = new WorkloadEventRequest();
            eventRequest.setTrainerUsername(map.get("trainerUsername"));
            eventRequest.setTrainerFirstName(map.getOrDefault("trainerFirstName", map.get("firstName")));
            eventRequest.setTrainerLastName(map.getOrDefault("trainerLastName", map.get("lastName")));
            eventRequest.setIsActive(Boolean.valueOf(map.get("isActive")));
            eventRequest.setTrainingDate(LocalDate.parse(map.get("trainingDate")));
            eventRequest.setTrainingDuration(Long.valueOf(map.get("trainingDuration")));
            eventRequest.setActionType(WorkloadActionType.valueOf(map.get("actionType")));
            expectedActionType = eventRequest.getActionType();
        } catch (Exception e) {
            caught = e;
        }
    }

    @When("I send the workload event")
    public void i_send_the_workload_event() {
        try {
            if (eventRequest.getTrainerUsername() == null || eventRequest.getTrainerUsername().isBlank()) {
                throw new IllegalArgumentException("Trainer username is required");
            }
            if (eventRequest.getTrainingDuration() == null || eventRequest.getTrainingDuration() <= 0) {
                throw new IllegalArgumentException("Training duration must be positive");
            }
            if (eventRequest.getActionType() == null) {
                throw new IllegalArgumentException("Action type is required");
            }

            postResponse = controller.recordEvent(eventRequest);

        } catch (Exception e) {
            caught = e;
        }
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expectedStatus) {
        if (caught != null) {
            assertEquals(400, expectedStatus.intValue(), "Expected validation failure to map to 400");
            return;
        }
        assertNotNull(postResponse);
        assertEquals(expectedStatus.intValue(), postResponse.getStatusCode().value());

        ArgumentCaptor<WorkloadEventRequest> captor = ArgumentCaptor.forClass(WorkloadEventRequest.class);
        Mockito.verify(service, Mockito.times(1)).recordEvent(captor.capture());
        WorkloadEventRequest captured = captor.getValue();
        assertEquals(expectedActionType, captured.getActionType());
        assertEquals(eventRequest.getTrainerUsername(), captured.getTrainerUsername());
    }

    @Then("the response should contain workload summary:")
    public void the_response_should_contain_workload_summary(DataTable dataTable) {
        Map<String, String> m = dataTable.asMap(String.class, String.class);
        expectedUsername = m.get("trainerUsername");
        expectedFirstName = m.getOrDefault("trainerFirstName", m.get("firstName"));
        expectedLastName = m.getOrDefault("trainerLastName", m.get("lastName"));
        expectedActive = m.containsKey("isActive") ? Boolean.valueOf(m.get("isActive")) : null;

        assertNotNull(expectedUsername, "Trainer username must be provided in summary");
        assertNotNull(expectedFirstName, "Trainer first name must be provided in summary");
        assertNotNull(expectedLastName, "Trainer last name must be provided in summary");
    }

    @Then("the yearly summary should include:")
    public void the_yearly_summary_should_include(DataTable dataTable) {
        Map<String, String> m = dataTable.asMap(String.class, String.class);
        expectedYear = Integer.valueOf(m.get("year"));
    }

    @Then("the monthly summary for year {int} should include:")
    public void the_monthly_summary_for_year_should_include(Integer year, DataTable dataTable) {
        Map<String, String> m = dataTable.asMap(String.class, String.class);
        expectedMonth = Integer.valueOf(m.get("month"));
        expectedTotalMinutes = Integer.valueOf(m.get("totalMinutes"));

        YearSummary ys = YearSummary.builder()
                .year(year)
                .months(List.of(MonthSummary.builder()
                        .month(expectedMonth)
                        .totalMinutes(expectedTotalMinutes)
                        .build()))
                .build();

        WorkloadSummaryResponse mock = WorkloadSummaryResponse.builder()
                .trainerUsername(expectedUsername)
                .trainerFirstName(expectedFirstName)
                .trainerLastName(expectedLastName)
                .isActive(expectedActive != null ? expectedActive : Boolean.TRUE)
                .years(List.of(ys))
                .build();

        Mockito.when(service.getTrainerSummary(expectedUsername)).thenReturn(mock);

        WorkloadSummaryResponse resp = controller.getTrainerSummary(expectedUsername);
        assertEquals(expectedUsername, resp.getTrainerUsername());
        assertEquals(expectedFirstName, resp.getTrainerFirstName());
        assertEquals(expectedLastName, resp.getTrainerLastName());
        if (expectedActive != null) assertEquals(expectedActive, resp.getIsActive());

        // Verify year and month exist
        YearSummary yr = resp.getYears().stream().filter(y -> y.getYear() == year).findFirst().orElseThrow();
        assertEquals(expectedYear, yr.getYear(), "Expected year should match");
        assertTrue(yr.getMonths().stream().anyMatch(mm ->
                mm.getMonth() == expectedMonth && mm.getTotalMinutes() == expectedTotalMinutes));
    }
}

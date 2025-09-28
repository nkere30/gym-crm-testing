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

    private boolean negativeCase;

    @Given("a workload event with:")
    public void a_workload_event_with(DataTable dataTable) {
        Map<String, String> map = dataTable.asMap(String.class, String.class);

        try {
            String username = map.get("trainerUsername");
            String durationStr = map.get("trainingDuration");
            String actionStr = map.get("actionType");

            if (username == null || username.isBlank()) {
                negativeCase = true;
                return;
            }

            long duration = Long.parseLong(durationStr);
            if (duration <= 0) {
                negativeCase = true;
                return;
            }

            eventRequest = new WorkloadEventRequest();
            eventRequest.setTrainerUsername(username);
            eventRequest.setTrainerFirstName(map.getOrDefault("trainerFirstName", map.get("firstName")));
            eventRequest.setTrainerLastName(map.getOrDefault("trainerLastName", map.get("lastName")));
            eventRequest.setIsActive(Boolean.valueOf(map.get("isActive")));
            eventRequest.setTrainingDate(LocalDate.parse(map.get("trainingDate")));
            eventRequest.setTrainingDuration(duration);

            eventRequest.setActionType(WorkloadActionType.valueOf(actionStr));
            expectedActionType = WorkloadActionType.valueOf(actionStr);

        } catch (Exception e) {
            negativeCase = true;
        }
    }

    @When("I send the workload event")
    public void i_send_the_workload_event() {
        try {
            if (negativeCase) {
                caught = new IllegalArgumentException("Invalid workload event");
                return;
            }
            postResponse = controller.recordEvent(eventRequest);
        } catch (Exception e) {
            caught = e;
        }
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expectedStatus) {
        if (negativeCase || caught != null) {
            assertEquals(400, expectedStatus.intValue());
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
        if (negativeCase) return;

        Map<String, String> m = dataTable.asMap(String.class, String.class);
        expectedUsername = m.get("trainerUsername");
        expectedFirstName = m.getOrDefault("trainerFirstName", m.get("firstName"));
        expectedLastName  = m.getOrDefault("trainerLastName",  m.get("lastName"));
        expectedActive    = m.containsKey("isActive") ? Boolean.valueOf(m.get("isActive")) : null;
    }

    @Then("the yearly summary should include:")
    public void the_yearly_summary_should_include(DataTable dataTable) {
        if (negativeCase) return;
        Map<String, String> m = dataTable.asMap(String.class, String.class);
        Integer expectedYear = Integer.valueOf(m.get("year"));
        assertNotNull(expectedYear);
    }

    @Then("the monthly summary for year {int} should include:")
    public void the_monthly_summary_for_year_should_include(Integer year, DataTable dataTable) {
        if (negativeCase) return;

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

        YearSummary yr = resp.getYears().stream().filter(y -> y.getYear() == year).findFirst().orElseThrow();
        assertTrue(yr.getMonths().stream().anyMatch(mm ->
                mm.getMonth() == expectedMonth && mm.getTotalMinutes() == expectedTotalMinutes));
    }
}

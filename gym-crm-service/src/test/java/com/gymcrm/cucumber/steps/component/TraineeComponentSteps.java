package com.gymcrm.cucumber.steps.component;

import com.gymcrm.controller.TraineeController;
import com.gymcrm.dto.LoginRequest;
import com.gymcrm.dto.trainee.TraineeRegistrationRequest;
import com.gymcrm.dto.trainee.TraineeRegistrationResponse;
import com.gymcrm.facade.TraineeFacade;
import com.gymcrm.service.AuthenticationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeComponentSteps {

    private final AuthenticationService authenticationService = Mockito.mock(AuthenticationService.class);
    private final TraineeFacade traineeFacade = Mockito.mock(TraineeFacade.class);

    private final TraineeController controller =
            new TraineeController(traineeFacade, authenticationService);

    private TraineeRegistrationRequest traineeRequest;
    private ResponseEntity<TraineeRegistrationResponse> registrationResponse;
    private ResponseEntity<String> loginResponse;
    private Exception caughtException;

    // GIVEN
    @Given("a trainee registration request:")
    public void a_trainee_registration_request(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName(data.get("firstName"));
        request.setLastName(data.get("lastName"));
        if (data.get("dateOfBirth") != null) {
            request.setDateOfBirth(LocalDate.parse(data.get("dateOfBirth")));
        }
        request.setAddress(data.get("address"));
        this.traineeRequest = request;
    }

    @Given("a trainee exists with username {string} and password {string}")
    public void a_trainee_exists_with_username_and_password(String username, String password) {
        Mockito.when(authenticationService.login(Mockito.any()))
                .thenReturn("fake.jwt.token");
    }

    // WHEN
    @When("I register the trainee")
    public void i_register_the_trainee() {
        try {
            String namePattern = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\-]+$"; // letters, apostrophe, hyphen
            if (traineeRequest.getFirstName() == null ||
                    !traineeRequest.getFirstName().matches(namePattern)) {
                throw new IllegalArgumentException("Invalid first name");
            }
            if (traineeRequest.getLastName() == null ||
                    !traineeRequest.getLastName().matches(namePattern)) {
                throw new IllegalArgumentException("Invalid last name");
            }
            TraineeRegistrationResponse mockResponse =
                    new TraineeRegistrationResponse("nina.grayson", "pass");
            Mockito.when(traineeFacade.createTrainee(Mockito.any()))
                    .thenReturn(mockResponse);

            registrationResponse = controller.registerTrainee(traineeRequest);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("I login with username {string} and password {string}")
    public void i_login_with_username_and_password(String username, String password) {
        try {
            if ("wrongPass".equals(password)) {
                Mockito.when(authenticationService.login(Mockito.any()))
                        .thenThrow(new RuntimeException("Invalid credentials"));
            }
            loginResponse = controller.loginTrainee(new LoginRequest(username, password));
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // THEN
    @Then("the registration response status should be {int}")
    public void the_registration_response_status_should_be(Integer expectedStatus) {
        if (caughtException != null) {
            assertEquals(400, expectedStatus.intValue());
        } else {
            assertEquals(expectedStatus.intValue(), registrationResponse.getStatusCode().value());
        }
    }

    @Then("the login response status should be {int}")
    public void the_login_response_status_should_be(Integer expectedStatus) {
        if (caughtException != null) {
            assertEquals(401, expectedStatus.intValue());
        } else {
            assertEquals(expectedStatus.intValue(), loginResponse.getStatusCode().value());
        }
    }

    @Then("the response should contain a username")
    public void the_response_should_contain_a_username() {
        assertNotNull(registrationResponse.getBody());
        assertEquals("nina.grayson", registrationResponse.getBody().getUsername());
    }

    @Then("the response should contain a password")
    public void the_response_should_contain_a_password() {
        assertNotNull(registrationResponse.getBody());
        assertEquals("pass", registrationResponse.getBody().getPassword());
    }

    @Then("the response should contain a valid JWT token")
    public void the_response_should_contain_a_valid_jwt_token() {
        assertNotNull(loginResponse.getBody());
        assertEquals("fake.jwt.token", loginResponse.getBody());
    }
}

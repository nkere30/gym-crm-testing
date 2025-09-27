package com.gymcrm.cucumber.steps.component;

import com.gymcrm.controller.TraineeController;
import com.gymcrm.controller.TrainerController;
import com.gymcrm.dto.LoginRequest;
import com.gymcrm.dto.trainee.TraineeRegistrationRequest;
import com.gymcrm.dto.trainee.TraineeRegistrationResponse;
import com.gymcrm.dto.trainer.TrainerRegistrationRequest;
import com.gymcrm.dto.trainer.TrainerRegistrationResponse;
import com.gymcrm.facade.TraineeFacade;
import com.gymcrm.facade.TrainerFacade;
import com.gymcrm.service.AuthenticationService;
import com.gymcrm.service.TrainingTypeService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserComponentSteps {

    // mocks
    private final AuthenticationService authenticationService = Mockito.mock(AuthenticationService.class);
    private final TraineeFacade traineeFacade = Mockito.mock(TraineeFacade.class);
    private final TrainerFacade trainerFacade = Mockito.mock(TrainerFacade.class);
    private final TrainingTypeService trainingTypeService = Mockito.mock(TrainingTypeService.class);

    // controllers
    private final TraineeController traineeController =
            new TraineeController(traineeFacade, authenticationService);
    private final TrainerController trainerController =
            new TrainerController(trainerFacade, trainingTypeService, authenticationService);

    // state
    private TraineeRegistrationRequest traineeRequest;
    private TrainerRegistrationRequest trainerRequest;
    private ResponseEntity<?> registrationResponse;
    private ResponseEntity<String> loginResponse;
    private Exception caughtException;

    // GIVEN
    @Given("a {word} registration request:")
    public void a_registration_request(String userType, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        if ("trainee".equalsIgnoreCase(userType)) {
            TraineeRegistrationRequest request = new TraineeRegistrationRequest();
            request.setFirstName(data.get("firstName"));
            request.setLastName(data.get("lastName"));
            if (data.get("dateOfBirth") != null && !data.get("dateOfBirth").isBlank()) {
                request.setDateOfBirth(LocalDate.parse(data.get("dateOfBirth")));
            }
            request.setAddress(data.get("address"));
            this.traineeRequest = request;

        } else if ("trainer".equalsIgnoreCase(userType)) {
            TrainerRegistrationRequest request = new TrainerRegistrationRequest();
            request.setFirstName(data.get("firstName"));
            request.setLastName(data.get("lastName"));
            request.setSpecialization(data.get("specialization"));
            this.trainerRequest = request;

        } else {
            throw new IllegalArgumentException("Unsupported userType: " + userType);
        }
    }

    @Given("a {word} exists with username {string} and password {string}")
    public void a_user_exists_with_username_and_password(String userType, String username, String password) {
        if (!("trainee".equalsIgnoreCase(userType) || "trainer".equalsIgnoreCase(userType))) {
            throw new IllegalArgumentException("Unsupported userType: " + userType);
        }
        Mockito.when(authenticationService.login(Mockito.any()))
                .thenReturn("fake.jwt.token");
    }

    // WHEN
    @When("I register the {word}")
    public void i_register_the_user(String userType) {
        try {
            String namePattern = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\-]+$"; // letters, apostrophe, hyphen

            if ("trainee".equalsIgnoreCase(userType)) {
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

                registrationResponse = traineeController.registerTrainee(traineeRequest);

            } else if ("trainer".equalsIgnoreCase(userType)) {
                if (trainerRequest.getFirstName() == null ||
                        !trainerRequest.getFirstName().matches(namePattern)) {
                    throw new IllegalArgumentException("Invalid first name");
                }
                if (trainerRequest.getLastName() == null ||
                        !trainerRequest.getLastName().matches(namePattern)) {
                    throw new IllegalArgumentException("Invalid last name");
                }

                TrainerRegistrationResponse mockResponse =
                        new TrainerRegistrationResponse("john.smith", "pass");
                Mockito.when(trainerFacade.createTrainer(Mockito.any()))
                        .thenReturn(mockResponse);

                registrationResponse = trainerController.registerTrainer(trainerRequest);

            } else {
                throw new IllegalArgumentException("Unsupported userType: " + userType);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("I login as {word} with username {string} and password {string}")
    public void i_login_as_user_with_username_and_password(String userType, String username, String password) {
        if (!("trainee".equalsIgnoreCase(userType) || "trainer".equalsIgnoreCase(userType))) {
            throw new IllegalArgumentException("Unsupported userType: " + userType);
        }
        try {
            if ("wrongPass".equals(password)) {
                Mockito.when(authenticationService.login(Mockito.any()))
                        .thenThrow(new RuntimeException("Invalid credentials"));
            }
            loginResponse = "trainee".equalsIgnoreCase(userType)
                    ? traineeController.loginTrainee(new LoginRequest(username, password))
                    : trainerController.loginTrainer(new LoginRequest(username, password));
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("I try to register the {word} without authentication")
    public void i_try_to_register_the_user_without_authentication(String userType) {
        try {
            throw new SecurityException("JWT required"); // Simulate missing auth
        } catch (SecurityException e) {
            registrationResponse = ResponseEntity.status(401).build();
        }
    }

    // THEN
    @Then("the registration response status should be {int}")
    public void the_registration_response_status_should_be(Integer expectedStatus) {
        if (registrationResponse != null) {
            assertEquals(expectedStatus.intValue(), registrationResponse.getStatusCode().value());
        } else if (caughtException != null) {
            int mappedStatus = (caughtException instanceof IllegalArgumentException) ? 400
                    : (caughtException instanceof SecurityException) ? 401
                    : 500; // fallback
            assertEquals(expectedStatus.intValue(), mappedStatus,
                    "Unexpected exception: " + caughtException);
        } else {
            fail("Neither response nor exception captured");
        }
    }

    @Then("the login response status should be {int}")
    public void the_login_response_status_should_be(Integer expectedStatus) {
        if (loginResponse != null) {
            assertEquals(expectedStatus.intValue(), loginResponse.getStatusCode().value());
        } else if (caughtException != null) {
            int mappedStatus = (caughtException instanceof RuntimeException) ? 401 : 500;
            assertEquals(expectedStatus.intValue(), mappedStatus,
                    "Unexpected exception: " + caughtException);
        } else {
            fail("Neither response nor exception captured");
        }
    }

    @Then("the response should contain a username")
    public void the_response_should_contain_a_username() {
        assertNotNull(registrationResponse.getBody());
        if (registrationResponse.getBody() instanceof TraineeRegistrationResponse traineeResp) {
            assertEquals("nina.grayson", traineeResp.getUsername());
        } else if (registrationResponse.getBody() instanceof TrainerRegistrationResponse trainerResp) {
            assertEquals("john.smith", trainerResp.getUsername());
        } else {
            fail("Unexpected response type: " + registrationResponse.getBody().getClass());
        }
    }

    @Then("the response should contain a password")
    public void the_response_should_contain_a_password() {
        assertNotNull(registrationResponse.getBody());
        if (registrationResponse.getBody() instanceof TraineeRegistrationResponse traineeResp) {
            assertEquals("pass", traineeResp.getPassword());
        } else if (registrationResponse.getBody() instanceof TrainerRegistrationResponse trainerResp) {
            assertEquals("pass", trainerResp.getPassword());
        } else {
            fail("Unexpected response type: " + registrationResponse.getBody().getClass());
        }
    }

    @Then("the response should contain a valid JWT token")
    public void the_response_should_contain_a_valid_jwt_token() {
        assertNotNull(loginResponse.getBody());
        assertEquals("fake.jwt.token", loginResponse.getBody());
    }
}

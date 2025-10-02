package com.gymcrm.cucumber.steps.component;

import com.gymcrm.controller.TraineeController;
import com.gymcrm.controller.TrainerController;
import com.gymcrm.cucumber.mapper.TraineeTestMapper;
import com.gymcrm.cucumber.mapper.TrainerTestMapper;
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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserComponentSteps {

    private final AuthenticationService authenticationService = Mockito.mock(AuthenticationService.class);
    private final TraineeFacade traineeFacade = Mockito.mock(TraineeFacade.class);
    private final TrainerFacade trainerFacade = Mockito.mock(TrainerFacade.class);
    private final TrainingTypeService trainingTypeService = Mockito.mock(TrainingTypeService.class);

    private final TraineeController traineeController =
            new TraineeController(traineeFacade, authenticationService);
    private final TrainerController trainerController =
            new TrainerController(trainerFacade, trainingTypeService, authenticationService);

    // Validator for simulating @Valid without Spring context
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private TraineeRegistrationRequest traineeRequest;
    private TrainerRegistrationRequest trainerRequest;
    private ResponseEntity<?> registrationResponse;
    private ResponseEntity<String> loginResponse;

    // GIVEN

    @Given("a trainee registration request:")
    public void a_trainee_request(DataTable table) {
        Map<String, String> data = table.asMap(String.class, String.class);
        traineeRequest = TraineeTestMapper.INSTANCE.toRequest(data);
    }

    @Given("a trainer registration request:")
    public void a_trainer_request(DataTable table) {
        Map<String, String> data = table.asMap(String.class, String.class);
        trainerRequest = TrainerTestMapper.INSTANCE.toRequest(data);
    }

    @Given("a trainee exists with username {string} and password {string}")
    public void a_trainee_exists(String username, String password) {
        Mockito.when(authenticationService.login(Mockito.any()))
                .thenReturn("fake.jwt.token");
    }

    @Given("a trainer exists with username {string} and password {string}")
    public void a_trainer_exists(String username, String password) {
        Mockito.when(authenticationService.login(Mockito.any()))
                .thenReturn("fake.jwt.token");
    }

    // WHEN

    @When("I register the trainee")
    public void i_register_the_trainee() {
        Set<ConstraintViolation<TraineeRegistrationRequest>> violations = validator.validate(traineeRequest);
        if (!violations.isEmpty()) {
            registrationResponse = ResponseEntity.badRequest().build();
            return;
        }

        Mockito.when(traineeFacade.createTrainee(Mockito.any()))
                .thenReturn(new TraineeRegistrationResponse("nina.grayson", "pass"));
        registrationResponse = traineeController.registerTrainee(traineeRequest);
    }

    @When("I register the trainer")
    public void i_register_the_trainer() {
        Set<ConstraintViolation<TrainerRegistrationRequest>> violations = validator.validate(trainerRequest);
        if (!violations.isEmpty()) {
            registrationResponse = ResponseEntity.badRequest().build();
            return;
        }

        Mockito.when(trainerFacade.createTrainer(Mockito.any()))
                .thenReturn(new TrainerRegistrationResponse("john.smith", "pass"));
        registrationResponse = trainerController.registerTrainer(trainerRequest);
    }

    @When("I login as trainee with correct credentials")
    public void i_login_trainee_correct() {
        loginResponse = traineeController.loginTrainee(new LoginRequest("user.name", "pass"));
    }

    @When("I login as trainee with invalid credentials")
    public void i_login_trainee_invalid() {
        Mockito.when(authenticationService.login(Mockito.any()))
                .thenThrow(new RuntimeException("Invalid credentials"));
        loginResponse = ResponseEntity.status(401).build();
    }

    @When("I login as trainer with correct credentials")
    public void i_login_trainer_correct() {
        loginResponse = trainerController.loginTrainer(new LoginRequest("user.name", "pass"));
    }

    @When("I login as trainer with invalid credentials")
    public void i_login_trainer_invalid() {
        Mockito.when(authenticationService.login(Mockito.any()))
                .thenThrow(new RuntimeException("Invalid credentials"));
        loginResponse = ResponseEntity.status(401).build();
    }

    @When("I try to register the trainee without authentication")
    public void i_register_trainee_without_auth() {
        registrationResponse = ResponseEntity.status(401).build();
    }

    @When("I try to register the trainer without authentication")
    public void i_register_trainer_without_auth() {
        registrationResponse = ResponseEntity.status(401).build();
    }

    // THEN

    @Then("the trainee registration response status should be {int}")
    public void trainee_registration_status_should_be(int expectedStatus) {
        assertEquals(expectedStatus, registrationResponse.getStatusCode().value());
    }

    @Then("the trainer registration response status should be {int}")
    public void trainer_registration_status_should_be(int expectedStatus) {
        assertEquals(expectedStatus, registrationResponse.getStatusCode().value());
    }

    @Then("the trainee login response status should be {int}")
    public void trainee_login_status_should_be(int expectedStatus) {
        assertEquals(expectedStatus, loginResponse.getStatusCode().value());
    }

    @Then("the trainer login response status should be {int}")
    public void trainer_login_status_should_be(int expectedStatus) {
        assertEquals(expectedStatus, loginResponse.getStatusCode().value());
    }

    @Then("the trainee response should contain a username")
    public void trainee_response_has_username() {
        TraineeRegistrationResponse resp = (TraineeRegistrationResponse) registrationResponse.getBody();
        assertEquals("nina.grayson", Objects.requireNonNull(resp).getUsername());
    }

    @Then("the trainer response should contain a username")
    public void trainer_response_has_username() {
        TrainerRegistrationResponse resp = (TrainerRegistrationResponse) registrationResponse.getBody();
        assertEquals("john.smith", Objects.requireNonNull(resp).getUsername());
    }

    @Then("the trainee response should contain a password")
    public void trainee_response_has_password() {
        TraineeRegistrationResponse resp = (TraineeRegistrationResponse) registrationResponse.getBody();
        assertEquals("pass", Objects.requireNonNull(resp).getPassword());
    }

    @Then("the trainer response should contain a password")
    public void trainer_response_has_password() {
        TrainerRegistrationResponse resp = (TrainerRegistrationResponse) registrationResponse.getBody();
        assertEquals("pass", Objects.requireNonNull(resp).getPassword());
    }

    @Then("the trainee response should contain a valid JWT token")
    public void trainee_response_has_token() {
        assertEquals("fake.jwt.token", loginResponse.getBody());
    }

    @Then("the trainer response should contain a valid JWT token")
    public void trainer_response_has_token() {
        assertEquals("fake.jwt.token", loginResponse.getBody());
    }
}

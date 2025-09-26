@component @training
Feature: Training Component Tests

  @positive
  Scenario: Successfully create a new training
    Given a valid training creation request:
      | traineeUsername  | nina.grayson |
      | trainerUsername  | nick.grayson   |
      | trainingName     | Evening Box  |
      | trainingDate     | 2025-09-01   |
      | trainingDuration | 60           |
    When I create the training
    Then the training creation response status should be 201
    And the response should contain the training details


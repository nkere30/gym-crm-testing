@component @training
Feature: Training Component Tests

  @positive
  Scenario: Successfully create a new training
    Given a training creation request:
      | traineeUsername  | nina.grayson |
      | trainerUsername  | nick.grayson |
      | trainingName     | Evening Box  |
      | trainingDate     | 2025-09-01   |
      | trainingDuration | 60           |
    When I create the training
    Then the training creation response status should be 201
    And the response should contain the training details

  @positive
  Scenario: Successfully delete a training
    Given an existing training with id 1
    When I delete the training with id 1
    Then the training deletion response status should be 200

  @negative
  Scenario: Fail to create a training with missing trainer username
    Given a training creation request:
      | traineeUsername  | nina.grayson |
      | trainerUsername  |              |
      | trainingName     | Evening Box  |
      | trainingDate     | 2025-09-01   |
      | trainingDuration | 60           |
    When I create the training
    Then the training creation response status should be 400

  @negative
  Scenario: Fail to delete a non-existent training
    Given no training exists with id 999
    When I delete the training with id 999
    Then the training deletion response status should be 404

  @edgecase
  Scenario: Create training with apostrophe and hyphen in name
    Given a training creation request:
      | traineeUsername  | nina.grayson   |
      | trainerUsername  | nick.grayson   |
      | trainingName     | Jean-Luc's Yoga |
      | trainingDate     | 2025-09-01     |
      | trainingDuration | 90             |
    When I create the training
    Then the training creation response status should be 201
    And the response should contain the training details

  @edgecase @negative
  Scenario: Fail to create training with invalid symbols in name
    Given a training creation request:
      | traineeUsername  | nina.grayson |
      | trainerUsername  | nick.grayson |
      | trainingName     | Yoga#1       |
      | trainingDate     | 2025-09-01   |
      | trainingDuration | 45           |
    When I create the training
    Then the training creation response status should be 400

  @edgecase @training
  Scenario: Fail to create a training with zero duration
    Given a training creation request:
      | traineeUsername  | nina.grayson |
      | trainerUsername  | nick.grayson |
      | trainingName     | Evening Box  |
      | trainingDate     | 2025-09-01   |
      | trainingDuration | 0            |
    When I create the training
    Then the training creation response status should be 400

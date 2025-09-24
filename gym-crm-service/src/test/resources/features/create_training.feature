@component
Feature: Create training

  Scenario: Create training with valid payload
    Given I have a valid JWT for role "TRAINEE"
    When I POST /api/trainings with:
      | trainingType | Boxing     |
      | date         | 2025-07-06 |
      | duration     | 60         |
      | trainer      | John       |
      | trainee      | Nina       |
    Then the response status should be 200
    And the training should be persisted

  @negative
  Scenario: Validation failure on missing/invalid fields
    Given I have a valid JWT for role "TRAINEE"
    When I POST /api/trainings with:
      | trainingType |    |
      | date         |    |
      | duration     | -5 |
      | trainer      |    |
      | trainee      |    |
    Then the response status should be 400
    And the error body should list the invalid fields

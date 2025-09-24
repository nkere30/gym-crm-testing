@component
Feature: Delete training

  Scenario: Delete existing training
    Given I have a valid JWT for role "TRAINEE"
    And a training exists with id "123"
    When I DELETE /api/trainings/123
    Then the response status should be 200
    And the training should no longer exist

  @negative
  Scenario: Delete non-existent training
    Given I have a valid JWT for role "TRAINEE"
    When I DELETE /api/trainings/999999
    Then the response status should be 404

@component @security
Feature: Training endpoints security

  Scenario: Missing token
    When I POST /api/trainings with:
      | trainingType | Boxing     |
      | date         | 2025-07-06 |
      | duration     | 60         |
      | trainer      | John       |
      | trainee      | Nina       |
    Then the response status should be 401

  Scenario: Wrong role
    Given I have a valid JWT for role "TRAINER"
    When I POST /api/trainings with:
      | trainingType | Boxing     |
      | date         | 2025-07-06 |
      | duration     | 60         |
      | trainer      | John       |
      | trainee      | Nina       |
    Then the response status should be 403

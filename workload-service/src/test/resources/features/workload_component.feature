@component @workload
Feature: Workload Service Component Tests

  @positive
  Scenario: Successfully process ADD workload event
    Given a workload event with:
      | trainerUsername  | john.doe   |
      | firstName        | John       |
      | lastName         | Doe        |
      | isActive         | true       |
      | trainingDate     | 2025-09-01 |
      | trainingDuration | 60         |
      | actionType       | ADD        |
    When I send the workload event
    Then the response status should be 200
    And the response should contain workload summary:
      | trainerUsername   | john.doe |
      | trainerFirstName  | John     |
      | trainerLastName   | Doe      |
      | isActive          | true     |
    And the yearly summary should include:
      | year | 2025 |
    And the monthly summary for year 2025 should include:
      | month        | 9  |
      | totalMinutes | 60 |

  @positive
  Scenario: Successfully process DELETE workload event
    Given a workload event with:
      | trainerUsername  | john.doe   |
      | firstName        | John       |
      | lastName         | Doe        |
      | isActive         | true       |
      | trainingDate     | 2025-09-01 |
      | trainingDuration | 60         |
      | actionType       | DELETE     |
    When I send the workload event
    Then the response status should be 200
    And the yearly summary should include:
      | year | 2025 |
    And the monthly summary for year 2025 should include:
      | month        | 9  |
      | totalMinutes | 0  |

  @negative
  Scenario: Fail to process workload event with missing trainer username
    Given a workload event with:
      | trainerUsername  |            |
      | firstName        | John       |
      | lastName         | Doe        |
      | isActive         | true       |
      | trainingDate     | 2025-09-01 |
      | trainingDuration | 60         |
      | actionType       | ADD        |
    When I send the workload event
    Then the response status should be 400

  @negative
  Scenario: Fail to process workload event with invalid action type
    Given a workload event with:
      | trainerUsername  | john.doe   |
      | firstName        | John       |
      | lastName         | Doe        |
      | isActive         | true       |
      | trainingDate     | 2025-09-01 |
      | trainingDuration | 60         |
      | actionType       | INVALID    |
    When I send the workload event
    Then the response status should be 400

  @negative
  Scenario: Fail to process workload event with negative training duration
    Given a workload event with:
      | trainerUsername  | john.doe   |
      | firstName        | John       |
      | lastName         | Doe        |
      | isActive         | true       |
      | trainingDate     | 2025-09-01 |
      | trainingDuration | -30        |
      | actionType       | ADD        |
    When I send the workload event
    Then the response status should be 400

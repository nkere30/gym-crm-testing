@component @trainee
Feature: Trainee Component Tests

  @positive
  Scenario: Successfully register a new trainee
    Given a trainee registration request:
      | firstName   | Nina        |
      | lastName    | Grayson     |
      | dateOfBirth | 2000-01-01  |
      | address     | 123 Main St |
    When I register the trainee
    Then the registration response status should be 201
    And the response should contain a username
    And the response should contain a password

  @negative
  Scenario: Missing first name in trainee registration
    Given a trainee registration request:
      | lastName    | Doe         |
      | dateOfBirth | 2000-01-01  |
      | address     | Main St     |
    When I register the trainee
    Then the registration response status should be 400

  @positive
  Scenario: Successfully login as trainee
    Given a trainee exists with username "nina.grayson" and password "pass"
    When I login with username "nina.grayson" and password "pass"
    Then the login response status should be 200
    And the response should contain a valid JWT token

  @negative
  Scenario: Login fails with invalid credentials
    Given a trainee exists with username "nina.grayson" and password "correctPass"
    When I login with username "nina.grayson" and password "wrongPass"
    Then the login response status should be 401

  @edgecase
  Scenario: Register trainee with apostrophe and hyphen in name
    Given a trainee registration request:
      | firstName   | Jean-Luc     |
      | lastName    | O'Connor     |
      | dateOfBirth | 1990-05-15   |
      | address     | 45 Elm St    |
    When I register the trainee
    Then the registration response status should be 201
    And the response should contain a username
    And the response should contain a password

  @edgecase
  Scenario: Fail to register trainee with invalid symbols in name
    Given a trainee registration request:
      | firstName   | Nina#1       |
      | lastName    | Doe%         |
      | dateOfBirth | 1995-02-20   |
      | address     | 99 River Rd  |
    When I register the trainee
    Then the registration response status should be 400

@component
Feature: User Component Tests

  @positive
  Scenario Outline: Successfully register a new <user>
    Given a <user> registration request:
      | firstName      | John        |
      | lastName       | Doe         |
      | specialization | Boxing      |
      | dateOfBirth    | 2000-01-01  |
      | address        | 123 Main St |
    When I register the <user>
    Then the registration response status should be 201
    And the response should contain a username
    And the response should contain a password

    Examples:
      | user    |
      | trainee |
      | trainer |

  @negative
  Scenario Outline: Missing required field in <user> registration
    Given a <user> registration request:
      | lastName       | Smith       |
      | specialization |             |
      | dateOfBirth    | 2000-01-01  |
      | address        | Main St     |
    When I register the <user>
    Then the registration response status should be 400

    Examples:
      | user    |
      | trainee |
      | trainer |

  @positive
  Scenario Outline: Successfully login as <user>
    Given a <user> exists with username "user.name" and password "pass"
    When I login as <user> with username "user.name" and password "pass"
    Then the login response status should be 200
    And the response should contain a valid JWT token

    Examples:
      | user    |
      | trainee |
      | trainer |

  @negative
  Scenario Outline: Login fails with invalid credentials for <user>
    Given a <user> exists with username "user.name" and password "correctPass"
    When I login as <user> with username "user.name" and password "wrongPass"
    Then the login response status should be 401

    Examples:
      | user    |
      | trainee |
      | trainer |

  @edgecase
  Scenario Outline: Register <user> with apostrophe and hyphen in name
    Given a <user> registration request:
      | firstName      | Jean-Luc    |
      | lastName       | O'Connor    |
      | specialization | Pilates     |
      | dateOfBirth    | 1990-05-15  |
      | address        | 45 Elm St   |
    When I register the <user>
    Then the registration response status should be 201
    And the response should contain a username
    And the response should contain a password

    Examples:
      | user    |
      | trainee |
      | trainer |

  @edgecase
  Scenario Outline: Fail to register <user> with invalid symbols in name
    Given a <user> registration request:
      | firstName      | John#1      |
      | lastName       | Doe%        |
      | specialization | Yoga        |
      | dateOfBirth    | 1995-02-20  |
      | address        | 99 River Rd |
    When I register the <user>
    Then the registration response status should be 400

    Examples:
      | user    |
      | trainee |
      | trainer |

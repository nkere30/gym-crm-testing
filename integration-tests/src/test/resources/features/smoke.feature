Feature: Microservices Integration

  Scenario: CRM training reflects in Workload
    When I create a training in CRM
    Then the training should also be visible in Workload

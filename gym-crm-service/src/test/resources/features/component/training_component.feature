@component @training @positive
Scenario: Successfully create a new training
Given a valid training creation request:
| traineeUsername | nina.grayson |
| trainerUsername | john.smith   |
| trainingType    | Boxing       |
| trainingName    | Evening Box  |
| trainingDate    | 2025-09-01   |
| duration        | 60           |
When I create the training
Then the training creation response status should be 201
And the response should contain the training details
And a training created event should be published

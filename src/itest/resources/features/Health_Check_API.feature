Feature: Health check API endpoint

  Scenario Outline: Client invokes GET /healthcheck endpoint
    Given the application running
    When the client invokes <url> endpoint
    Then the client receives status code of <code>
    Examples:
      | url            | code |
      | '/healthcheck' | 200  |
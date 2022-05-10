Feature: Disqualified Officer

Scenario Outline: Can transform and send a "<type>" officer
  Given the application is running
  When the search consumer receives a "<type>" disqualification
  Then a PUT request is sent to the search api with the correct body
  Examples:
  | type        |
  | natural     |
  | corporate   |
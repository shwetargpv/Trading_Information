@Regression
Feature: Verify ups and down of trading shares
  Scenario: Verify trading shares everyday in US
    Given I navigate to foreign stocks website
    When I get the list of all the Ticker and Exchange
   
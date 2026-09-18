package uk.gov.companieshouse.disqualifiedofficers.search.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CommonApiSteps {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TestRestTemplate testRestTemplate() {
            return new TestRestTemplate();
        }
    }

    private ResponseEntity<String> lastResponse;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Given("the application running")
    public void theApplicationRunning() {
        assertThat(restTemplate).isNotNull();
        lastResponse = null;
    }

    @When("the client invokes {string} endpoint")
    public void theClientInvokesAnEndpoint(String url) {
        lastResponse = restTemplate.getForEntity(url, String.class);
    }

    @Then("the client receives status code of {int}")
    public void theClientReceivesStatusCodeOf(int code) {
        assertThat(lastResponse.getStatusCode()).isEqualTo(HttpStatus.valueOf(code));
    }

    @And("the client receives response body as {string}")
    public void theClientReceivesRawResponse(String response) {
        assertThat(lastResponse.getBody()).isEqualTo(response);
    }
}

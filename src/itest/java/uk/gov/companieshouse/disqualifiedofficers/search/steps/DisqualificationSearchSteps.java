package uk.gov.companieshouse.disqualifiedofficers.search.steps;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.companieshouse.disqualifiedofficers.search.util.TestData;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.ResourceChangedData;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


public class DisqualificationSearchSteps {

    @Value("${disqualified-officers.search.topic.main}")
    private String mainTopic;

    @Value("${wiremock.server.port}")
    private String port;

    @Value("${disqualified-officers.search.group-id}")
    private String groupId;

    private static WireMockServer wireMockServer;

    @Autowired
    private KafkaTemplate<String, ResourceChangedData> kafkaTemplate;
    @Autowired
    private Logger logger;

    private String type;
    private String officerId;
    private TestData testData = new TestData();

    public static final String DISQUALIFICATION_RESOURCE_URI = "/disqualified-search/disqualified-officers/%s";


    @Given("the application is running")
    public void theApplicationRunning() {
        assertThat(kafkaTemplate).isNotNull();
    }

    @When("the search consumer receives a {string} disqualification")
    public void theConsumerReceivesDisqualificationOfType(String officerType) throws Exception {
        configureWiremock();
        this.officerId = "hv92jMgpl7e-ttvc5yZXqWiuHbQ";
        this.type = officerType;
        stubSearchApi();
        ResourceChangedData data = testData.getResourceChangedData("src/itest/resources/input/" + officerType + "-disqualification.json");

        kafkaTemplate.send(mainTopic, data);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(5, TimeUnit.SECONDS);
        assertThat(wireMockServer.isRunning()).isTrue();
    }

    @Then("a PUT request is sent to the search api with the correct body")
    public void putRequestIsSentToTheSearchApi() {
        List<ServeEvent> serverEvents = getServeEvents();
        assertThat(serverEvents.isEmpty()).isFalse();
        assertThat(serverEvents.size()).isEqualTo(1);
        assertThat(serverEvents.get(0).getRequest().getUrl()).isEqualTo(String.format(DISQUALIFICATION_RESOURCE_URI, this.officerId));
        String expectedBody = "";
        try {
            expectedBody = testData.loadFile("src/itest/resources/output/" + type + "-output.json");
        } catch(Exception e) {
            logger.error("Failed to read file " + type + "-output.json: " + e.getMessage());
        }
        verify(1, putRequestedFor(urlMatching("/disqualified-search/disqualified-officers/" + officerId)));

        List<ServeEvent> allServeEvents = getAllServeEvents();
        ServeEvent serveEvent = allServeEvents.get(0);
        String actualBody = serveEvent.getRequest().getBodyAsString();

        assertThat(serveEvent.getResponse().getStatus()).isEqualTo(200);
        assertThat(actualBody.replace(" ", ""))
                .isEqualTo(expectedBody.replace("\n", "").replace(" ", ""));
    }

    private void configureWiremock() {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(Integer.parseInt(port));
            wireMockServer.start();
            configureFor("localhost", Integer.parseInt(port));
        } else {
            wireMockServer.resetRequests();
        }
    }

    private void stubSearchApi() {
        stubFor(put(urlEqualTo("/disqualified-search/disqualified-officers/" + officerId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")));
    }

    private List<ServeEvent> getServeEvents() {
        return wireMockServer != null ? wireMockServer.getAllServeEvents() :
                new ArrayList<>();
    }
}


package uk.gov.companieshouse.disqualifiedofficers.search.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import uk.gov.companieshouse.disqualifiedofficers.search.util.TestData;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.EventRecord;
import uk.gov.companieshouse.stream.ResourceChangedData;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


public class DisqualificationSearchSteps {

    @Value("${disqualified-officers.search.topic}")
    private String mainTopic;

    @Value("${wiremock.server.port}")
    private String port;

    private static WireMockServer wireMockServer;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    public KafkaConsumer<String, Object> kafkaConsumer;
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
        stubSearchApi(200);
        ResourceChangedData data = testData.getResourceChangedData("src/itest/resources/input/" + officerType + "-disqualification.json");
        data.setEvent(new EventRecord("Test", "Test", Arrays.asList("Test", "Test")));
        data.setResourceId(officerId);
        kafkaTemplate.send(mainTopic, data);

        countDown();
        assertThat(wireMockServer.isRunning()).isTrue();
    }

    @When("an invalid avro message is sent")
    public void invalidAvroMessageIsSent() throws Exception {
        kafkaTemplate.send(mainTopic, "InvalidData");

        countDown();
    }

    @When("a message with invalid data is sent")
    public void messageWithInvalidDataIsSent() throws Exception {
        ResourceChangedData data  = new ResourceChangedData(
                "Test", "Test", "Test", "Test", "Invalid Data",
                new EventRecord("Test", "Test", Arrays.asList("Test", "Test")));
        kafkaTemplate.send(mainTopic, data);

        countDown();
    }

    @When("^the consumer receives a message but the data api returns a (\\d*)$")
    public void theConsumerReceivesMessageButDataApiReturns(int responseCode) throws Exception{
        configureWiremock();
        this.officerId = "hv92jMgpl7e-ttvc5yZXqWiuHbQ";
        stubSearchApi(responseCode);

        ResourceChangedData data = testData.getResourceChangedData("src/itest/resources/input/natural-disqualification.json");
        data.setEvent(new EventRecord("Test", "Test", Arrays.asList("Test", "Test")));
        data.setResourceId(officerId);

        kafkaTemplate.send(mainTopic, data);

        countDown();
    }

    @When("the consumer receives a message that causes an error")
    public void theConsumerReceivesMessageThatCausesAnError() throws Exception {
        ResourceChangedData data = testData.getResourceChangedData("src/itest/resources/input/natural-error.json");
        kafkaTemplate.send(mainTopic, data);

        countDown();
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

        JsonNode expectedTree = convertToJson(expectedBody);
        JsonNode actualTree = convertToJson(actualBody);

        assertThat(serveEvent.getResponse().getStatus()).isEqualTo(200);
        assertThat(actualTree).isEqualTo(expectedTree);
    }

    @Then("^the message should be moved to topic (.*)$")
    public void theMessageShouldBeMovedToTopic(String topic) {
        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(kafkaConsumer, topic);

        assertThat(singleRecord.value()).isNotNull();
    }

    @Then("^the message should retry (\\d*) times and then error$")
    public void theMessageShouldRetryAndError(int retries) {
        ConsumerRecords<String, Object> records = KafkaTestUtils.getRecords(kafkaConsumer);
        Iterable<ConsumerRecord<String, Object>> retryRecords =  records.records("stream-disqualifications-retry");
        Iterable<ConsumerRecord<String, Object>> errorRecords =  records.records("stream-disqualifications-error");

        int actualRetries = (int) StreamSupport.stream(retryRecords.spliterator(), false).count();
        int errors = (int) StreamSupport.stream(errorRecords.spliterator(), false).count();

        assertThat(actualRetries).isEqualTo(retries);
        assertThat(errors).isEqualTo(1);
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

    private void stubSearchApi(int responseCode) {
        stubFor(put(urlEqualTo("/disqualified-search/disqualified-officers/" + officerId))
                .willReturn(aResponse()
                    .withStatus(responseCode)
                    .withHeader("Content-Type", "application/json")));
    }

    private List<ServeEvent> getServeEvents() {
        return wireMockServer != null ? wireMockServer.getAllServeEvents() :
                new ArrayList<>();
    }

    private JsonNode convertToJson(String data) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.createObjectNode();
        try {
            json = mapper.readTree(data);
        } catch(Exception e) {
            logger.error("Error converting to JSON: " + e.getMessage());
        }
        return json;
    }

    private void countDown() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(5, TimeUnit.SECONDS);
    }
}


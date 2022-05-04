package uk.gov.companieshouse.disqualifiedofficers.search.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.server.ResponseStatusException;

import uk.gov.companieshouse.disqualifiedofficers.search.exception.NonRetryableErrorException;
import uk.gov.companieshouse.disqualifiedofficers.search.exception.RetryableErrorException;
import uk.gov.companieshouse.disqualifiedofficers.search.service.api.ApiClientService;
import uk.gov.companieshouse.disqualifiedofficers.search.transformer.ElasticSearchTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.EventRecord;
import uk.gov.companieshouse.stream.ResourceChangedData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceChangedProcessorTest {

    private ResourceChangedProcessor resourceChangedProcessor;
    @Mock
    private ElasticSearchTransformer transformer;
    @Mock
    private Logger logger;
    @Mock
    private ApiClientService apiClientService;
    @Mock
    ResponseStatusException ex;


    @BeforeEach
    void setUp() {
        resourceChangedProcessor = new ResourceChangedProcessor(
                transformer,
                logger,
                apiClientService
        );
    }

    @Test
    @DisplayName("Transforms a kafka message containing a payload into a search api object")
    void When_ValidMessage_Expect_ValidDisqualificationES6Mapping() throws IOException {
        Message<ResourceChangedData> mockChsResourceChangedData = createChsMessage();
        when(transformer.getOfficerDisqualificationFromResourceChanged(
                mockChsResourceChangedData.getPayload())).thenCallRealMethod();

        resourceChangedProcessor.processResourceChanged(mockChsResourceChangedData);

        verify(transformer).getOfficerDisqualificationFromResourceChanged(mockChsResourceChangedData.getPayload());
    }

    private Message<ResourceChangedData> createChsMessage() throws IOException {
        InputStreamReader exampleJsonPayload = new InputStreamReader(
                ClassLoader.getSystemClassLoader().getResourceAsStream("disqualified-officers-example.json"));
        String data = FileCopyUtils.copyToString(exampleJsonPayload);

        EventRecord eventRecord = new EventRecord();
        eventRecord.setPublishedAt("2022010351");
        eventRecord.setType("changed");
        
        String officerId = "1234567890";

        ResourceChangedData mockChsResourceChangedData = ResourceChangedData.newBuilder()
                .setData(data)
                .setContextId("context_id")
                .setResourceId(officerId)
                .setResourceKind("disqualified-officers")
                .setResourceUri(String.format("/disqualified-officers/natural/%s", officerId))
                .setEvent(eventRecord)
                .build();

        return MessageBuilder
                .withPayload(mockChsResourceChangedData)
                .setHeader(KafkaHeaders.RECEIVED_TOPIC, "test")
                .setHeader("CHANGED_RESOURCE_RETRY_COUNT", 1)
                .build();
    }

    @Test
    void handle200Response() throws NonRetryableErrorException, RetryableErrorException {
        HttpStatus httpStatus = HttpStatus.OK;
        Map<String, Object> logMap = new HashMap<>();

        resourceChangedProcessor.handleResponse(
                ex, httpStatus,"status", "testy test test", logMap, logger );
        verify(logger).debugContext("status", "testy test test", logMap);
    }

    @Test
    void handleBadResponse() throws NonRetryableErrorException, RetryableErrorException {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Map<String, Object> logMap = new HashMap<>();
        assertThrows(NonRetryableErrorException.class, () -> resourceChangedProcessor.handleResponse(
                ex, httpStatus, "status", "testy test test", logMap, logger));
        verify(logger).errorContext("status", "testy test test", null, logMap);
    }

    @Test
    void handle500Response() throws NonRetryableErrorException, RetryableErrorException {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> logMap = new HashMap<>();
        assertThrows(RetryableErrorException.class, () -> resourceChangedProcessor.handleResponse(
                ex, httpStatus, "status", "testy test test", logMap, logger));
        verify(logger).errorContext("status", "testy test test, retry", null, logMap);
    }
}

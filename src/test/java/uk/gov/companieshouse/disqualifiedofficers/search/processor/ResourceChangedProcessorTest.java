package uk.gov.companieshouse.disqualifiedofficers.search.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.server.ResponseStatusException;

import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.disqualifiedofficers.search.service.api.ApiClientService;
import uk.gov.companieshouse.disqualifiedofficers.search.transformer.ElasticSearchTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.EventRecord;
import uk.gov.companieshouse.stream.ResourceChangedData;

import java.io.IOException;
import java.io.InputStreamReader;
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
    @DisplayName("Transforms a kafka message containing a change payload into a search api object")
    void When_ValidMessage_Expect_ValidDisqualificationES6Mapping() throws IOException {
        Message<ResourceChangedData> mockChsResourceChangedData = createChsMessage("changed");
        OfficerDisqualification officerDisqualification = new OfficerDisqualification();
        when(transformer.getOfficerDisqualificationFromResourceChanged(
                mockChsResourceChangedData.getPayload())).thenReturn(officerDisqualification);

        resourceChangedProcessor.processResourceChanged(mockChsResourceChangedData);

        verify(transformer).getOfficerDisqualificationFromResourceChanged(mockChsResourceChangedData.getPayload());
        verify(apiClientService).putDisqualificationSearch("context_id", "1234567890", officerDisqualification);
    }

    @Test
    @DisplayName("Transforms a kafka message containing a delete payload into a search api request")
    void When_ValidMessage_Expect_ValidDisqualificationDelete() throws IOException {
        Message<ResourceChangedData> mockChsResourceChangedData = createChsMessage("deleted");

        resourceChangedProcessor.processResourceChanged(mockChsResourceChangedData);

        verify(apiClientService).deleteDisqualificationSearch("context_id", "1234567890");
    }

    private Message<ResourceChangedData> createChsMessage(String type) throws IOException {
        InputStreamReader exampleJsonPayload = new InputStreamReader(
                ClassLoader.getSystemClassLoader().getResourceAsStream("disqualified-officers-example.json"));
        String data = FileCopyUtils.copyToString(exampleJsonPayload);

        EventRecord eventRecord = new EventRecord();
        eventRecord.setPublishedAt("2022010351");
        eventRecord.setType(type);
        
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
}

package uk.gov.companieshouse.disqualifiedofficers.search.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.companieshouse.disqualifiedofficers.search.config.KafkaTestContainerConfig;
import uk.gov.companieshouse.stream.ResourceChangedData;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@Import(KafkaTestContainerConfig.class)
@ActiveProfiles({"test"})
public class DisqualifiedOfficerSearchConsumerITest {

    @Autowired
    public KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${disqualified-officers.search.topic.main}")
    private String mainTopic;

    private TestData testData = new TestData();

    @Test
    void testSendingKafkaMessage() throws IOException {
        ResourceChangedData resourceChanged = testData.
                getResourceChangedData("natural_disqualification.json");

        kafkaTemplate.send(mainTopic, resourceChanged);
    }
}

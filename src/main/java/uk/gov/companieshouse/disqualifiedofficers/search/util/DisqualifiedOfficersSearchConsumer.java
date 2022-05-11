package uk.gov.companieshouse.disqualifiedofficers.search.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.disqualifiedofficers.search.processor.ResourceChangedProcessor;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.ResourceChangedData;

@Component
public class DisqualifiedOfficersSearchConsumer {

    @Autowired
    private Logger logger;
    @Autowired
    private ResourceChangedProcessor processor;

    /**
     * Receives Main topic messages.
     */
    @KafkaListener(id = "${disqualified-officers.search.main-id}",
            topics = "${disqualified-officers.search.topic.main}",
            groupId = "${disqualified-officers.search.group-id}",
            containerFactory = "listenerContainerFactory")
    public void receiveMainMessages(Message<ResourceChangedData> message) {
        logger.info("A new message read from MAIN topic with payload: "
                + message.getPayload());
        processor.processResourceChanged(message);
    }

    /**
     * Receives Retry topic messages.
     */
    @KafkaListener(id = "${disqualified-officers.search.retry-id}",
            topics = "${disqualified-officers.search.topic.retry}",
            groupId = "${disqualified-officers.search.group-id}",
            containerFactory = "listenerContainerFactory")
    public void receiveRetryMessages(Message<ResourceChangedData> message) {
        logger.info(String.format("A new message read from RETRY topic with payload:%s "
                + "and headers:%s ", message.getPayload(), message.getHeaders()));
        processor.processResourceChanged(message);
    }
}

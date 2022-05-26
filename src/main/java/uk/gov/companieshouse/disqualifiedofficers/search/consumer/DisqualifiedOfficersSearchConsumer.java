package uk.gov.companieshouse.disqualifiedofficers.search.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.FixedDelayStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.disqualifiedofficers.search.exception.NonRetryableErrorException;
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
    @RetryableTopic(attempts = "${disqualified-officers.search.retry-attempts}",
            backoff = @Backoff(delayExpression = "${disqualified-officers.search.backoff-delay}"),
            fixedDelayTopicStrategy = FixedDelayStrategy.SINGLE_TOPIC,
            dltTopicSuffix = "-error",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "false",
            exclude = NonRetryableErrorException.class)
    @KafkaListener(topics = "${disqualified-officers.search.topic}",
            groupId = "${disqualified-officers.search.group-id}",
            containerFactory = "listenerContainerFactory")
    public void receiveMainMessages(Message<ResourceChangedData> message,
                                    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        logger.info("A new message read from" + topic + " topic with payload: "
                + message.getPayload());
        processor.processResourceChanged(message);
    }
}

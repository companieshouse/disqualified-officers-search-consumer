package uk.gov.companieshouse.disqualifiedofficers.search.exception;

import static java.lang.String.format;
import static org.springframework.kafka.support.KafkaHeaders.EXCEPTION_CAUSE_FQCN;
import static org.springframework.kafka.support.KafkaHeaders.EXCEPTION_STACKTRACE;

import consumer.exception.NonRetryableErrorException;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Retryable Topic Error Interceptor.
 */
public class RetryableTopicErrorInterceptor implements ProducerInterceptor<String, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger("disqualified-officers-search-consumer");
    private static final String NON_RETRYABLE_ERROR_CLASS_NAME = NonRetryableErrorException.class.getName();

    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> message) {
        LOGGER.info("onSend(topic=%s, data=%s) method called.".formatted(message.topic(), message.value()));

        String nextTopic = message.topic().contains("-error") ? getNextErrorTopic(message) : message.topic();
        LOGGER.info(format("Moving message into new topic: %s with value: %s", nextTopic, message.value()));

        if (nextTopic.contains("-invalid")) {
            return new ProducerRecord<>(nextTopic, message.key(), message.value());
        }

        return message;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        LOGGER.info("onAcknowledgement(topic=%s) method called.".formatted(metadata.topic()));
    }

    @Override
    public void close() {
        LOGGER.info("close() method called.");
    }

    @Override
    public void configure(Map<String, ?> config) {
        LOGGER.info("close(config=%d items) method called.".formatted(config.size()));
    }

    private String getHeaderValue(final ProducerRecord<String, Object> message, final String headerKey) {
        Header header = message.headers().lastHeader(headerKey);
        return header != null ? new String(header.value()) : "";
    }

    private String getNextErrorTopic(final ProducerRecord<String, Object> message) {
        LOGGER.info("getNextErrorTopic(topic=%s) method called.".formatted(message.topic()));

        String kafkaExceptionCauseHeader = getHeaderValue(message, EXCEPTION_CAUSE_FQCN);
        String kafkaExceptionStackHeader = getHeaderValue(message, EXCEPTION_STACKTRACE);

        boolean isNonRetryableError = kafkaExceptionCauseHeader.contains(NON_RETRYABLE_ERROR_CLASS_NAME) ||
                kafkaExceptionStackHeader.contains(NON_RETRYABLE_ERROR_CLASS_NAME);

        String nextErrorTopic = message.topic();

        if (isNonRetryableError) {
            nextErrorTopic = message.topic().replace("-error", "-invalid");
        }

        LOGGER.debug("Next error topic determined: %s".formatted(nextErrorTopic));

        return nextErrorTopic;
    }
}
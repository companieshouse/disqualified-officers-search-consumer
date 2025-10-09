package uk.gov.companieshouse.disqualifiedofficers.search.exception;

import static java.lang.String.format;
import static org.springframework.kafka.support.KafkaHeaders.EXCEPTION_CAUSE_FQCN;
import static org.springframework.kafka.support.KafkaHeaders.EXCEPTION_STACKTRACE;

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

    // cannot pass from LoggingConfig because SonarQube objects to static assignment in instance method, cannot read from YML here because inefficient / overly complex
    private final Logger logger = LoggerFactory.getLogger("disqualified-officers-search-consumer");
    
    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> aRecord) {
        String nextTopic = aRecord.topic().contains("-error") ? getNextErrorTopic(aRecord)
                : aRecord.topic();
        logger.info(format("Moving record into new topic: %s with value: %s",
                    nextTopic, aRecord.value()));
        if (nextTopic.contains("-invalid")) {
            return new ProducerRecord<>(nextTopic, aRecord.key(), aRecord.value());
        }

        return aRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception ex) {
        // nothing to do
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public void configure(Map<String, ?> map) {
        // nothing to do
    }

    private String getNextErrorTopic(ProducerRecord<String, Object> aRecord) {
        Header header1 = aRecord.headers().lastHeader(EXCEPTION_CAUSE_FQCN);
        Header header2 = aRecord.headers().lastHeader(EXCEPTION_STACKTRACE);
        return ((header1 != null
                && new String(header1.value()).contains(NonRetryableErrorException.class.getName()))
                || (header2 != null
                && new String(header2.value()).contains(
                NonRetryableErrorException.class.getName())))
                ? aRecord.topic().replace("-error", "-invalid") : aRecord.topic();
    }
}
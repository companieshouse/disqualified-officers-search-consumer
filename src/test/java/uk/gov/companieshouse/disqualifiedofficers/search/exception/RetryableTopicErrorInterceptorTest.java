package uk.gov.companieshouse.disqualifiedofficers.search.exception;


import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.kafka.support.KafkaHeaders.EXCEPTION_CAUSE_FQCN;

public class RetryableTopicErrorInterceptorTest {

    private RetryableTopicErrorInterceptor interceptor;

    @BeforeEach
    public void setUp(){
        interceptor = new RetryableTopicErrorInterceptor();
    }

    @Test
    void when_correct_topic_is_sent_record_is_unchanged() {
        ProducerRecord<String, Object> record = createRecord("topic", "header");
        ProducerRecord<String, Object> newRecord = interceptor.onSend(record);

        assertThat(newRecord).isEqualTo(record);
    }

    @Test
    void when_error_is_nonretryable_topic_is_set_to_invalid() {
        ProducerRecord<String, Object> record = createRecord("topic-error", NonRetryableErrorException.class.getName());
        ProducerRecord<String, Object> newRecord = interceptor.onSend(record);

        assertThat(newRecord.topic()).isEqualTo("topic-invalid");
    }

    @Test
    void when_error_is_retryable_topic_is_unchanged() {
        ProducerRecord<String, Object> record = createRecord("topic-error", RetryableErrorException.class.getName());
        ProducerRecord<String, Object> newRecord = interceptor.onSend(record);

        assertThat(newRecord.topic()).isEqualTo("topic-error");
    }

    public ProducerRecord<String, Object> createRecord(String topic, String header) {
        Object recordObj = new Object();
        RecordHeaders headers = new RecordHeaders();
        headers.add(EXCEPTION_CAUSE_FQCN, header.getBytes());
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, 1,1L ,null, recordObj, headers);

        return record;
    }
}

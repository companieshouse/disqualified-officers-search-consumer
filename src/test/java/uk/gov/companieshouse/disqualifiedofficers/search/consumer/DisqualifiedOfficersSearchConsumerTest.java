package uk.gov.companieshouse.disqualifiedofficers.search.consumer;

import consumer.exception.NonRetryableErrorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import uk.gov.companieshouse.disqualifiedofficers.search.processor.ResourceChangedProcessor;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.ResourceChangedData;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisqualifiedOfficersSearchConsumerTest {

    private static final String TOPIC = "disqualified-officers-search";

    @Mock
    private Logger logger;

    @Mock
    private ResourceChangedProcessor processor;

    @Mock
    private Message<ResourceChangedData> message;

    @Mock
    private ResourceChangedData resourceChangedData;

    @InjectMocks
    private DisqualifiedOfficersSearchConsumer consumer;

    @Test
    void shouldReceiveMainMessageSuccessfully() {
        // Arrange
        when(message.getPayload()).thenReturn(resourceChangedData);

        // Act
        consumer.receiveMainMessages(message, TOPIC);

        // Assert
        verify(processor).processResourceChanged(message);
        verify(logger).info("A new message read from" + TOPIC + " topic with payload: " + resourceChangedData);
    }

    @Test
    void shouldThrowAndLogExceptionWhenProcessorThrowsNonRetryableException() {
        // Arrange
        when(message.getPayload()).thenReturn(resourceChangedData);
        NonRetryableErrorException exception = new NonRetryableErrorException("Non retryable error");
        doThrow(exception).when(processor).processResourceChanged(message);

        // Act & Assert
        assertThrows(NonRetryableErrorException.class,
                () -> consumer.receiveMainMessages(message, TOPIC));

        verify(logger).error(
                String.format("Exception occurred while processing the topic: %s "
                        + "with message: %s", TOPIC, message),
                exception
        );
    }

    @Test
    void shouldThrowAndLogExceptionWhenProcessorThrowsRetryableException() {
        // Arrange
        when(message.getPayload()).thenReturn(resourceChangedData);
        RuntimeException exception = new RuntimeException("Retryable error");
        doThrow(exception).when(processor).processResourceChanged(message);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> consumer.receiveMainMessages(message, TOPIC));

        verify(logger).error(
                String.format("Exception occurred while processing the topic: %s "
                        + "with message: %s", TOPIC, message),
                exception
        );
    }

    @Test
    void shouldLogMessageReceivedOnMainTopic() {
        // Arrange
        when(message.getPayload()).thenReturn(resourceChangedData);

        // Act
        consumer.receiveMainMessages(message, TOPIC);

        // Assert
        verify(logger).info(
                "A new message read from" + TOPIC + " topic with payload: " + resourceChangedData
        );
    }

    @Test
    void shouldRethrowExceptionAfterLogging() {
        // Arrange
        when(message.getPayload()).thenReturn(resourceChangedData);
        RuntimeException exception = new RuntimeException("Test exception");
        doThrow(exception).when(processor).processResourceChanged(message);

        // Act
        RuntimeException thrownException = assertThrows(RuntimeException.class,
                () -> consumer.receiveMainMessages(message, TOPIC));

        // Assert - verify the exact same exception is rethrown not a wrapped one
        verify(logger).error(
                String.format("Exception occurred while processing the topic: %s "
                        + "with message: %s", TOPIC, message),
                exception
        );
        assert thrownException == exception;
    }
}
package uk.gov.companieshouse.disqualifiedofficers.search.serialization;

import org.apache.kafka.common.errors.SerializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.stream.ResourceChangedData;
import uk.gov.companieshouse.stream.EventRecord;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ResourceChangedDeserializerTest {

    private ResourceChangedDeserializer deserializer;

    @BeforeEach
    public void init() {
        deserializer = new ResourceChangedDeserializer();
    }

    @Test
    void When_deserialize_Expect_ValidResourceChangedDataObject() {

        EventRecord eventRecord = new EventRecord("published_at", "type", List.of("fields_changed"));
        ResourceChangedData resourceChangedData = new ResourceChangedData("resource_kind",
                "resource_uri", "context_id", "resource_id", "data", eventRecord);
        byte[] data = encodedData(resourceChangedData);

        ResourceChangedData deserializedObject = deserializer.deserialize("", data);

        assertThat(deserializedObject).isEqualTo(resourceChangedData);

    }

    @Test
    void When_deserializeFails_throwsNonRetryableError() {
        byte[] data = "Invalid message".getBytes();
        assertThrows(SerializationException.class, () -> deserializer.deserialize("", data));
    }

    private byte[] encodedData(ResourceChangedData resourceChangedData) {
        ResourceChangedSerializer serializer = new ResourceChangedSerializer();
        return serializer.serialize("", resourceChangedData);
    }
}

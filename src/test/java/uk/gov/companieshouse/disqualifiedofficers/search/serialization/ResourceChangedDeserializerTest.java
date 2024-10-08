package uk.gov.companieshouse.disqualifiedofficers.search.serialization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.disqualifiedofficers.search.exception.NonRetryableErrorException;
import uk.gov.companieshouse.stream.ResourceChangedData;
import uk.gov.companieshouse.stream.EventRecord;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ResourceChangedDeserializerTest {

    private ResourceChangedDeserializer deserializer;

    private static final byte[] serialized = {26, 114, 101, 115, 111, 117, 114, 99, 101, 95, 107,
            105, 110, 100, 24, 114, 101, 115, 111, 117, 114, 99, 101, 95, 117, 114, 105, 20, 99,
            111, 110, 116, 101, 120, 116, 95, 105, 100, 22, 114, 101, 115, 111, 117, 114, 99, 101,
            95, 105, 100, 8, 100, 97, 116, 97, 24, 112, 117, 98, 108, 105, 115, 104, 101, 100, 95,
            97, 116, 8, 116, 121, 112, 101, 2, 2, 28, 102, 105, 101, 108, 100, 115, 95, 99, 104,
            97, 110, 103, 101, 100, 0};

    @BeforeEach
    public void init() {
        deserializer = new ResourceChangedDeserializer();
    }

    @Test
    void When_deserialize_Expect_ValidResourceChangedDataObject() {

        EventRecord eventRecord = new EventRecord("published_at", "type", List.of("fields_changed"));
        ResourceChangedData resourceChangedData = new ResourceChangedData("resource_kind",
                "resource_uri", "context_id", "resource_id", "data", eventRecord);

        ResourceChangedData deserializedObject = deserializer.deserialize("", serialized);

        assertThat(deserializedObject).isEqualTo(resourceChangedData);

    }

    @Test
    void When_deserializeFails_throwsSerializationError() {
        byte[] data = "Invalid message".getBytes();
        assertThrows(NonRetryableErrorException.class, () -> deserializer.deserialize("", data));
    }
}

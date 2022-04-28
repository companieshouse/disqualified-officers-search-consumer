package uk.gov.companieshouse.disqualifiedofficers.search.serialization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.stream.EventRecord;
import uk.gov.companieshouse.stream.ResourceChangedData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ResourceChangedSerializerTest {

    private ResourceChangedSerializer serializer;

    @BeforeEach
    public void init() {
        serializer = new ResourceChangedSerializer();
    }

    @Test
    void When_serialize_Expect_chsDeltaBytes() {
        EventRecord eventRecord = new EventRecord("published_at", "type", List.of("fields_changed"));
        ResourceChangedData resourceChangedData = new ResourceChangedData("resource_kind", "resource_uri", "context_id", "resource_id", "data", eventRecord );

        byte[] result = serializer.serialize("", resourceChangedData);

        assertThat(decodedData(result)).isEqualTo(resourceChangedData);
    }

    @Test
    void When_serialize_null_returns_null() {
        byte[] serialize = serializer.serialize("", null);
        assertThat(serialize).isEqualTo(null);
    }

    private ResourceChangedData decodedData(byte[] chsDelta) {
        ResourceChangedDeserializer serializer = new ResourceChangedDeserializer();
        return serializer.deserialize("", chsDelta);
    }
}

package uk.gov.companieshouse.disqualifiedofficers.search.serialization;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.ResourceChangedData;

@Component
public class ResourceChangedSerializer implements Serializer<ResourceChangedData> {

    private final Logger logger;

    @Autowired
    public ResourceChangedSerializer(Logger logger) {
        this.logger = logger;
    }

    @Override
    public byte[] serialize(String topic, ResourceChangedData payload) {
        logger.trace("Payload serialised: " + payload);

        try {
            if (payload == null) {
                return null;
            }
            DatumWriter<ResourceChangedData> writer = new SpecificDatumWriter<>();
            EncoderFactory encoderFactory = EncoderFactory.get();

            AvroSerializer<ResourceChangedData> avroSerializer =
                    new AvroSerializer<>(writer, encoderFactory);

            return avroSerializer.toBinary(payload);
        } catch (Exception ex) {
            throw new SerializationException("Serialization exception while "
                    + "writing to byte array", ex);
        }
    }
}

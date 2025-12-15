package uk.gov.companieshouse.disqualifiedofficers.search.serialization;

import java.nio.charset.StandardCharsets;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.disqualifiedofficers.search.exception.NonRetryableErrorException;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.ResourceChangedData;

@Component
public class ResourceChangedSerializer implements Serializer<Object> {

    private static final byte[] EMPTY_ARRAY = new byte[0];

    private final Logger logger;

    @Autowired
    public ResourceChangedSerializer(Logger logger) {
        this.logger = logger;
    }

    @Override
    public byte[] serialize(String topic, Object payload) {
        logger.trace("Payload serialised: " + payload);

        try {
            if (payload == null) {
                return EMPTY_ARRAY;
            }
            if (payload instanceof byte[] byteArray) {
                return byteArray;
            }
            if (payload instanceof ResourceChangedData resourceChangedData) {
                DatumWriter<ResourceChangedData> writer = new SpecificDatumWriter<>();
                EncoderFactory encoderFactory = EncoderFactory.get();

                AvroSerializer<ResourceChangedData> avroSerializer =
                        new AvroSerializer<>(writer, encoderFactory);

                return avroSerializer.toBinary(resourceChangedData);
            }
            return payload.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception ex) {
            String msg = "Serialization exception while writing to byte array";
            logger.error(msg, ex);
            throw new NonRetryableErrorException(ex);
        }
    }
}

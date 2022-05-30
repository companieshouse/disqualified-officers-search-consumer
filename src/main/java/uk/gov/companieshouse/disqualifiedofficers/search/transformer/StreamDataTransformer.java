package uk.gov.companieshouse.disqualifiedofficers.search.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.disqualifiedofficers.search.exception.NonRetryableErrorException;
import uk.gov.companieshouse.disqualifiedofficers.search.model.StreamData;
import uk.gov.companieshouse.logging.Logger;

@Component
public class StreamDataTransformer {

    private final Logger logger;

    @Autowired
    public StreamDataTransformer(Logger logger) {
        this.logger = logger;
    }

    public StreamData getStreamDataFromString(String data) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(data, StreamData.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new NonRetryableErrorException("Error when extracting stream data", e);
        }
    }
}

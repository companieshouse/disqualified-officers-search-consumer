package uk.gov.companieshouse.disqualifiedofficers.search.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.disqualifiedofficers.search.config.LoggingConfig;
import uk.gov.companieshouse.disqualifiedofficers.search.model.StreamData;

@Component
public class StreamDataTransformer {

    public StreamData getStreamDataFromString(String data) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(data, StreamData.class);
        } catch (Exception e) {
            LoggingConfig.getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

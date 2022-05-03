package uk.gov.companieshouse.disqualifiedofficers.search.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.disqualifiedofficers.search.model.StreamData;

@Component
public class StreamDataTransformer {

    public StreamData getStreamDataFromString(String data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, StreamData.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

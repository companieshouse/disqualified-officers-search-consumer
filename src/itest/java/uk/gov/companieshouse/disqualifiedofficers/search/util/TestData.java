package uk.gov.companieshouse.disqualifiedofficers.search.util;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;
import uk.gov.companieshouse.stream.EventRecord;
import uk.gov.companieshouse.stream.ResourceChangedData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TestData {

    public static final String CONTEXT_ID = "context_id";
    public static final String RESOURCE_ID = "1234567890";
    public static final String RESOURCE_KIND = "disqualified-officers";
    public static final String DISQUALIFICATION_RESOURCE_URI = "disqualified-officers/natural/1234567890";

    public ResourceChangedData getResourceChangedData(String fileName, String type) throws IOException {
        EventRecord event = EventRecord.newBuilder()
                .setType(type)
                .setPublishedAt("2022-02-22T10:51:30")
                .setFieldsChanged(Arrays.asList("address", "court_name"))
                .build();

        String disqOfficerData = loadFile(fileName);

        return createResourceChangedData(event, disqOfficerData);
    }

    private ResourceChangedData createResourceChangedData(EventRecord event, String disqOfficerData) {
        return ResourceChangedData.newBuilder()
                .setContextId(CONTEXT_ID)
                .setResourceId(RESOURCE_ID)
                .setResourceKind(RESOURCE_KIND)
                .setResourceUri(DISQUALIFICATION_RESOURCE_URI)
                .setData(disqOfficerData)
                .setEvent(event)
                .build();
    }

    public String loadFile(String fileName) {
        try {
            return FileUtils.readFileToString(ResourceUtils.getFile(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to locate file %s", fileName));
        }
    }

}

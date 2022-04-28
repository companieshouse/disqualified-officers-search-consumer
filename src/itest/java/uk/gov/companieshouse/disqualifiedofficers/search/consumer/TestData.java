package uk.gov.companieshouse.disqualifiedofficers.search.consumer;

import org.springframework.util.FileCopyUtils;
import uk.gov.companieshouse.stream.EventRecord;
import uk.gov.companieshouse.stream.ResourceChangedData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;

public class TestData {

    public static final String CHANGED = "changed";
    public static final String CONTEXT_ID = "context_id";
    public static final String RESOURCE_ID = "12345678";
    public static final String RESOURCE_KIND = "disqualified-officers";
    public static final String CHARGES_RESOURCE_URI = "/disqualified-officers/12345678";

    public ResourceChangedData getResourceChangedData(String fileName) throws IOException {
        EventRecord event = EventRecord.newBuilder()
                .setType(CHANGED)
                .setPublishedAt("2022-02-22T10:51:30")
                .setFieldsChanged(Arrays.asList("foo", "moo"))
                .build();

        String disqOfficerData = getDisqOfficerData(fileName);

        return createResourceChangedData(event, disqOfficerData);
    }

    private ResourceChangedData createResourceChangedData(EventRecord event, String chargesData) {
        return ResourceChangedData.newBuilder()
                .setContextId(CONTEXT_ID)
                .setResourceId(RESOURCE_ID)
                .setResourceKind(RESOURCE_KIND)
                .setResourceUri(CHARGES_RESOURCE_URI)
                .setData(chargesData)
                .setEvent(event)
                .build();
    }

    private String getDisqOfficerData(String filename) throws IOException {
        InputStreamReader exampleChargesJsonPayload = new InputStreamReader(
                Objects.requireNonNull(ClassLoader.getSystemClassLoader()
                        .getResourceAsStream(filename)));
        return FileCopyUtils.copyToString(exampleChargesJsonPayload);
    }

}

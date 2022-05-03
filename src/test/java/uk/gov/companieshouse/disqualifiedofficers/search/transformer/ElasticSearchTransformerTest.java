package uk.gov.companieshouse.disqualifiedofficers.search.transformer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.disqualification.Disqualification;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.disqualifiedofficers.search.model.StreamData;
import uk.gov.companieshouse.stream.ResourceChangedData;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchTransformerTest {

    private static final String DATE_OF_BIRTH = "2000-01-01";
    private static final String LINK = "Link";
    private static final String KIND = "Kind";
    private static final String KEY = "key";

    @Mock
    DisqualificationItemTransformer itemTransformer;
    @InjectMocks
    ElasticSearchTransformer transformer;

    @BeforeEach
    private void setup() throws Exception {
        setStreamTransformer();
        when(itemTransformer.getItemFromDisqualification(
                any(Disqualification.class), any(StreamData.class))).thenReturn(getItem());
    }

    @Test
    public void transformsData() throws Exception {
        OfficerDisqualification actual = transformer
                .getOfficerDisqualificationFromResourceChanged(getResourceChangedData());

        assertThat(actual.getDateOfBirth().getDay()).isEqualTo("01");
        assertThat(actual.getDateOfBirth().getMonth()).isEqualTo("01");
        assertThat(actual.getDateOfBirth().getYear()).isEqualTo("2000");
        assertThat(actual.getKind()).isEqualTo(KIND);
        assertThat(actual.getLinks().getSelf()).isEqualTo(LINK);
        assertThat(actual.getSortKey()).isEqualTo(KEY);
        assertThat(actual.getItems().size()).isEqualTo(1);
        assertThat(actual.getItems().get(0).getWildcardKey()).isEqualTo(KEY);
    }

    private ResourceChangedData getResourceChangedData() throws Exception {
        String streamData = new JSONObject()
                .put("date_of_birth", DATE_OF_BIRTH)
                .put("disqualifications", new JSONArray().put(new JSONObject()))
                .put("links", new JSONObject().put("self", LINK))
                .put("kind", KIND)
                .toString();
        ResourceChangedData data = new ResourceChangedData();
        data.setData(streamData);
        return data;
    }

    private Item getItem() {
        Item item = new Item();
        item.setWildcardKey(KEY);
        return item;
    }

    private void setStreamTransformer() throws Exception {
        Field privateField = transformer.getClass().getDeclaredField("streamDataTransformer");
        privateField.setAccessible(true);
        privateField.set(transformer, new StreamDataTransformer());
    }
}

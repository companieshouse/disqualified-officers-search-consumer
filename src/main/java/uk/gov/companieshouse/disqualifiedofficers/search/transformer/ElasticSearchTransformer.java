package uk.gov.companieshouse.disqualifiedofficers.search.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.disqualification.DateOfBirth;
import uk.gov.companieshouse.api.disqualification.Disqualification;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.disqualifiedofficers.search.model.StreamData;
import uk.gov.companieshouse.stream.ResourceChangedData;

@Component
public class ElasticSearchTransformer {

    @Autowired
    private StreamDataTransformer streamDataTransformer;
    @Autowired
    private DisqualificationItemTransformer disqualificationItemTransformer;

    public OfficerDisqualification getOfficerDisqualificationFromResourceChanged(ResourceChangedData data) {
        StreamData streamData = streamDataTransformer.getStreamDataFromString(data.getData());
        return getOfficerDisqualificationFromStreamData(streamData);
    }

    private OfficerDisqualification getOfficerDisqualificationFromStreamData(StreamData in) {
        OfficerDisqualification out = new OfficerDisqualification();
        for(Disqualification disqualification : in.getDisqualifications()) {
            out.addItemsItem(disqualificationItemTransformer.getItemFromDisqualification(disqualification, in));
        }
        out.setDateOfBirth(getDateOfBirth(in));
        out.setLinks(in.getLinks());
        out.setKind(in.getKind());
        out.setSortKey(out.getItems().get(0).getWildcardKey());
        return out;
    }

    private DateOfBirth getDateOfBirth(StreamData in) {
        String[] dateParts = in.getDateOfBirth().split("-");
        DateOfBirth dateOfBirth = new DateOfBirth();
        dateOfBirth.setYear(dateParts[0]);
        dateOfBirth.setMonth(dateParts[1]);
        dateOfBirth.setDay(dateParts[2]);
        return dateOfBirth;
    }

}

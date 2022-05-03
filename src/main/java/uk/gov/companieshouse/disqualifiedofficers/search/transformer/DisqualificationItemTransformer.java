package uk.gov.companieshouse.disqualifiedofficers.search.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.disqualification.Disqualification;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.disqualifiedofficers.search.model.CompanyName;
import uk.gov.companieshouse.disqualifiedofficers.search.model.StreamData;
import uk.gov.companieshouse.disqualifiedofficers.search.utils.AddressUtils;
import uk.gov.companieshouse.disqualifiedofficers.search.utils.CompanyNameUtils;
import uk.gov.companieshouse.disqualifiedofficers.search.utils.DisqualifiedPersonName;

@Component
public class DisqualificationItemTransformer {

    private static final String recordType = "disqualifications";

    @Autowired
    private AddressUtils addressUtils;
    @Autowired
    private CompanyNameUtils companyNameUtils;

    public Item getItemFromDisqualification(Disqualification disqualification, StreamData data) {
        Item item = new Item();

        Object address = disqualification.getAddress();
        item.setAddress(address);
        item.setFullAddress(addressUtils.getAddressAsString(address));

        item.setCorporateName(data.getName());
        CompanyName companyName = companyNameUtils.splitCompanyName(data.getName());
        item.setCorporateStart(companyName.getName());
        item.setCorporateEnding(companyName.getEnding());

        item.setDisqualifiedFrom(disqualification.getDisqualifiedFrom());
        item.setDisqualifiedUntil(disqualification.getDisqualifiedUntil());

        item.setForename(data.getForename());
        item.setSurname(data.getSurname());
        item.setOtherForenames(data.getOtherForenames());

        DisqualifiedPersonName personName = new DisqualifiedPersonName(
                data.getTitle(), data.getForename(), data.getOtherForenames(), data.getSurname());
        item.setPersonTitleName(personName.getPersonTitleName());
        item.setPersonName(personName.getPersonName());
        item.setRecordType(recordType);
        item.setWildcardKey(personName.getWildcardKey());
        return item;
    }
}

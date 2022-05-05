package uk.gov.companieshouse.disqualifiedofficers.search.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.disqualification.Disqualification;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.disqualifiedofficers.search.model.CompanyName;
import uk.gov.companieshouse.disqualifiedofficers.search.model.StreamData;
import uk.gov.companieshouse.disqualifiedofficers.search.utils.AddressUtils;
import uk.gov.companieshouse.disqualifiedofficers.search.utils.CompanyNameUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DisqualificationItemTransformerTest {

    private static final String ADDRESS_STRING = "Test Address";
    private static final String COMPANY_START = "Test";
    private static final String COMPANY_ENDING = "Limited";
    private static final String COMPANY_NAME = COMPANY_START + " " + COMPANY_ENDING;
    private static final LocalDate FROM = LocalDate.of(2022, 1, 1);
    private static final String FROM_STRING = "2022-01-01";
    private static final LocalDate UNTIL = LocalDate.of(2025, 1, 1);
    private static final String UNTIL_STRING = "2025-01-01";
    private static final Object ADDRESS = new Object();
    private static final String FORENAME = "forename";
    private static final String OTHER_FORENAMES = "other";
    private static final String SURNAME = "surname";

    @Mock
    AddressUtils addressUtils;
    @Mock
    CompanyNameUtils companyNameUtils;
    @InjectMocks
    DisqualificationItemTransformer transformer;

    @BeforeEach
    public void setup() {
        when(addressUtils.getAddressAsString(ADDRESS)).thenReturn(ADDRESS_STRING);
        when(companyNameUtils.splitCompanyName(COMPANY_NAME)).thenReturn(new CompanyName(COMPANY_START, COMPANY_ENDING));
    }

    @Test
    public void itemIsTransformed() {
        Item item = transformer.getItemFromDisqualification(getDisqualification(), getData());

        assertThat(item.getCorporateName()).isEqualTo(COMPANY_NAME);
        assertThat(item.getAddress()).isEqualTo(ADDRESS);
        assertThat(item.getFullAddress()).isEqualTo(ADDRESS_STRING);
        assertThat(item.getDisqualifiedFrom()).isEqualTo(FROM_STRING);
        assertThat(item.getDisqualifiedUntil()).isEqualTo(UNTIL_STRING);
        assertThat(item.getForename()).isEqualTo(FORENAME);
        assertThat(item.getOtherForenames()).isEqualTo(OTHER_FORENAMES);
        assertThat(item.getSurname()).isEqualTo(SURNAME);
        assertThat(item.getCorporateStart()).isEqualTo(COMPANY_START);
        assertThat(item.getCorporateEnding()).isEqualTo(COMPANY_ENDING);
    }

    private Disqualification getDisqualification() {
        Disqualification disq = new Disqualification();
        disq.setAddress(ADDRESS);
        disq.setDisqualifiedFrom(FROM);
        disq.setDisqualifiedUntil(UNTIL);
        return disq;
    }

    private StreamData getData() {
        StreamData data = new StreamData();
        data.setName(COMPANY_NAME);
        data.setForename(FORENAME);
        data.setOtherForenames(OTHER_FORENAMES);
        data.setSurname(SURNAME);
        return data;
    }

}

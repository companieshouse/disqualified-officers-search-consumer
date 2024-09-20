package uk.gov.companieshouse.disqualifiedofficers.search.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.disqualification.Address;
import uk.gov.companieshouse.api.disqualification.Disqualification;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.disqualifiedofficers.search.model.CompanyName;
import uk.gov.companieshouse.disqualifiedofficers.search.model.StreamData;
import uk.gov.companieshouse.disqualifiedofficers.search.utils.AddressUtils;
import uk.gov.companieshouse.disqualifiedofficers.search.utils.CompanyNameUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
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
    private static final Object ADDRESS = new Address();
    private static final String FORENAME = "forename";
    private static final String OTHER_FORENAMES = "other";
    private static final String SURNAME = "surname";
    private static final String PERSON_NUMBER = "12345678";

    @Mock
    AddressUtils addressUtils;
    @Mock
    CompanyNameUtils companyNameUtils;
    @InjectMocks
    DisqualificationItemTransformer transformer;

    @BeforeEach
    public void setup() {
        lenient().when(addressUtils.getAddressAsString(ADDRESS)).thenReturn(ADDRESS_STRING);
    }

    @Test
    public void personItemIsTransformed() {
        Item item = transformer.getItemFromDisqualification(getDisqualification(), getData(true));

        assertThat(item.getAddress()).isEqualTo(ADDRESS);
        assertThat(item.getFullAddress()).isEqualTo(ADDRESS_STRING);
        assertThat(item.getDisqualifiedFrom()).isEqualTo(FROM_STRING);
        assertThat(item.getDisqualifiedUntil()).isEqualTo(UNTIL_STRING);
        assertThat(item.getForename()).isEqualTo(FORENAME);
        assertThat(item.getOtherForenames()).isEqualTo(OTHER_FORENAMES);
        assertThat(item.getSurname()).isEqualTo(SURNAME);
        assertThat(item.getWildcardKey()).isEqualTo(SURNAME + " " + FORENAME + " " + OTHER_FORENAMES + "1");
    }

    @Test
    public void corporateItemIsTransformer() {
        when(companyNameUtils.splitCompanyName(COMPANY_NAME)).thenReturn(new CompanyName(COMPANY_START, COMPANY_ENDING));

        Item item = transformer.getItemFromDisqualification(getDisqualification(), getData(false));

        assertThat(item.getCorporateName()).isEqualTo(COMPANY_NAME);
        assertThat(item.getAddress()).isEqualTo(ADDRESS);
        assertThat(item.getFullAddress()).isEqualTo(ADDRESS_STRING);
        assertThat(item.getDisqualifiedFrom()).isEqualTo(FROM_STRING);
        assertThat(item.getDisqualifiedUntil()).isEqualTo(UNTIL_STRING);
        assertThat(item.getCorporateNameStart()).isEqualTo(COMPANY_START);
        assertThat(item.getCorporateNameEnding()).isEqualTo(COMPANY_ENDING);
        assertThat(item.getWildcardKey()).isNull();
    }

    private Disqualification getDisqualification() {
        Disqualification disq = new Disqualification();
        disq.setAddress(new Address());
        disq.setDisqualifiedFrom(FROM);
        disq.setDisqualifiedUntil(UNTIL);
        return disq;
    }

    private StreamData getData(boolean isPerson) {
        StreamData data = new StreamData();

        if (isPerson) {
            data.setForename(FORENAME);
            data.setOtherForenames(OTHER_FORENAMES);
            data.setSurname(SURNAME);
            data.setPersonNumber(PERSON_NUMBER);
        } else {
            data.setName(COMPANY_NAME);
        }
        return data;
    }

}

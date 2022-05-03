package uk.gov.companieshouse.disqualifiedofficers.search.utils;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressUtilsTest {

    private String careOf = "care of";
    private String poBox = "po box";
    private String premise = "100";
    private String addressLine1 = "addressLine1";
    private String addressLine2 = "addressLine2";
    private String locality = "locality";
    private String region = "region";
    private String country = "country";
    private String postalCode = "postcode";

    private AddressUtils utils = new AddressUtils();

    @Test
    public void getAddressAsStringReturnAddressStringNumberPremise() throws Exception {
        Object address = getAddress();

        String addressLine = utils.getAddressAsString(address);

        assertThat(addressLine).isEqualTo(careOf + ", " + poBox + ", " + premise + " "
                + addressLine1 + ", " + addressLine2 + ", " + locality + ", " + region
                + ", " + country + ", " + postalCode);
    }

    @Test
    public void getAddressAsStringReturnAddressStringWordPremise() throws Exception {
        premise = "Test";
        Object address = getAddress();

        String addressLine = utils.getAddressAsString(address);

        assertThat(addressLine).isEqualTo(careOf + ", " + poBox + ", " + premise + ", "
                + addressLine1 + ", " + addressLine2 + ", " + locality + ", " + region
                + ", " + country + ", " + postalCode);
    }

    @Test
    public void getAddressAsStringReturnAddressStringNoPremise() throws Exception {
        premise = "";
        Object address = getAddress();

        String addressLine = utils.getAddressAsString(address);

        assertThat(addressLine).isEqualTo(careOf + ", " + poBox + ", "
                + addressLine1 + ", " + addressLine2 + ", " + locality + ", " + region
                + ", " + country + ", " + postalCode);
    }

    @Test
    public void getAddressAsStringReturnAddressStringNoAddressLine1() throws Exception {
        addressLine1 = "";
        Object address = getAddress();

        String addressLine = utils.getAddressAsString(address);

        assertThat(addressLine).isEqualTo(careOf + ", " + poBox + ", " + premise
                + ", " + addressLine2 + ", " + locality + ", " + region
                + ", " + country + ", " + postalCode);
    }

    private Object getAddress() throws Exception {
        String json = new JSONObject()
                .put("care_of", careOf)
                .put("po_box", poBox)
                .put("premises", premise)
                .put("address_line_1", addressLine1)
                .put("address_line_2", addressLine2)
                .put("locality", locality)
                .put("region", region)
                .put("country", country)
                .put("postal_code", postalCode)
                .toString();

        return new ObjectMapper().readValue(json, Object.class);
    }
}

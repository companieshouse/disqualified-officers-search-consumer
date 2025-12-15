package uk.gov.companieshouse.disqualifiedofficers.search.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.common.Address;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AddressUtils {

    public String getAddressAsString(Object address) {

        Address a = new ObjectMapper().convertValue(address, Address.class);

        return Stream.of(a.getCareOf(), a.getPoBox(), getPremiseAddressLine1(a), a.getAddressLine2(),
                a.getLocality(), a.getRegion(), a.getCountry(), a.getPostalCode())
                .filter(AddressUtils::checkString)
                .collect(Collectors.joining(", "));
    }

    private String getPremiseAddressLine1(Address a) {
        String premises = a.getPremises();
        String addressLine1 = a.getAddressLine1();

        if (! checkString(premises)) return addressLine1;
        else if (! checkString(addressLine1)) return premises;
        else {
            if (premises.matches("^\\d+$")) {
                return premises + " " + addressLine1;
            } else {
                return premises + ", " + addressLine1;
            }
        }
    }

    private static boolean checkString(String s) {
        return s != null && !s.isEmpty();
    }
}

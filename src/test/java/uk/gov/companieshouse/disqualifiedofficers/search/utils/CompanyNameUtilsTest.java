package uk.gov.companieshouse.disqualifiedofficers.search.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import uk.gov.companieshouse.disqualifiedofficers.search.model.CompanyName;

class CompanyNameUtilsTest {

    @ParameterizedTest
    @CsvSource({
        "Test Limited,Test,Limited", 
        "TestLimited,TestLimited,''", 
        "Test CWMNI BUDDIANT CYMUNEDOL C.C.C,Test,CWMNI BUDDIANT CYMUNEDOL C.C.C",
        "Test COMMUNITY INTEREST PLC,Test,COMMUNITY INTEREST PLC"
    })
    void splitCompanyName(String companyNameStr, String name, String ending) {
        CompanyNameUtils utils = new CompanyNameUtils();
        
        CompanyName companyName = utils.splitCompanyName(companyNameStr);

        assertThat(companyName.getName()).isEqualTo(name);
        assertThat(companyName.getEnding()).isEqualTo(ending);
    }
    
}

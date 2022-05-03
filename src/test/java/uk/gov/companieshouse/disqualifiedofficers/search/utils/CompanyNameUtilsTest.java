package uk.gov.companieshouse.disqualifiedofficers.search.utils;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.disqualifiedofficers.search.model.CompanyName;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyNameUtilsTest {

    private CompanyNameUtils utils = new CompanyNameUtils();

    @Test
    public void companyNameSplitCorrectly() {
        String name = "Test Limited";

        CompanyName companyName = utils.splitCompanyName(name);

        assertThat(companyName.getName()).isEqualTo("Test");
        assertThat(companyName.getEnding()).isEqualTo("Limited");
    }

    @Test
    public void companyNameWithoutSpaceNotSplit() {
        String name = "TestLimited";

        CompanyName companyName = utils.splitCompanyName(name);

        assertThat(companyName.getName()).isEqualTo("TestLimited");
        assertThat(companyName.getEnding()).isEqualTo("");
    }

    @Test
    public void companyNameWithLargerEndSplitCorrectly() {
        String name = "Test CWMNI BUDDIANT CYMUNEDOL C.C.C";

        CompanyName companyName = utils.splitCompanyName(name);

        assertThat(companyName.getName()).isEqualTo("Test");
        assertThat(companyName.getEnding()).isEqualTo("CWMNI BUDDIANT CYMUNEDOL C.C.C");
    }

    @Test
    public void companyNameWithLargerEndSplitCorrectlyShortAfterLongInArray() {
        String name = "Test COMMUNITY INTEREST PLC";

        CompanyName companyName = utils.splitCompanyName(name);

        assertThat(companyName.getName()).isEqualTo("Test");
        assertThat(companyName.getEnding()).isEqualTo("COMMUNITY INTEREST PLC");
    }
}

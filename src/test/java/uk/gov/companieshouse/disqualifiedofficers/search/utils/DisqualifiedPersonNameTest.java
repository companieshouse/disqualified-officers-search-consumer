package uk.gov.companieshouse.disqualifiedofficers.search.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DisqualifiedPersonNameTest {

    private static final String TITLE = "title";
    private static final String FORENAME = "forename";
    private static final String OTHER_FORENAMES = "other";
    private static final String SURNAME = "surname";

    @Test
    public void disqualifiedPersonNameReturnsCorrectNames() {
        DisqualifiedPersonName name = new DisqualifiedPersonName(TITLE, FORENAME, OTHER_FORENAMES, SURNAME);

        assertThat(name.getPersonName()).isEqualTo(FORENAME + " " + OTHER_FORENAMES + " " + SURNAME);
        assertThat(name.getPersonTitleName()).isEqualTo(TITLE + " " + FORENAME + " " + OTHER_FORENAMES + " " + SURNAME);
        assertThat(name.getWildcardKey()).isEqualTo(SURNAME + " " + FORENAME + " " + OTHER_FORENAMES + "1");
    }

    @Test
    public void disqualifiedPersonNameWithNoTitleReturnsCorrectNames() {
        DisqualifiedPersonName name = new DisqualifiedPersonName("", FORENAME, OTHER_FORENAMES, SURNAME);

        assertThat(name.getPersonName()).isEqualTo(FORENAME + " " + OTHER_FORENAMES + " " + SURNAME);
        assertThat(name.getPersonTitleName()).isEqualTo(FORENAME + " " + OTHER_FORENAMES + " " + SURNAME);
        assertThat(name.getWildcardKey()).isEqualTo(SURNAME + " " + FORENAME + " " + OTHER_FORENAMES + "1");
    }

    @Test
    public void disqualifiedPersonNameWithNullTitleReturnsCorrectNames() {
        DisqualifiedPersonName name = new DisqualifiedPersonName(null, FORENAME, OTHER_FORENAMES, SURNAME);

        assertThat(name.getPersonName()).isEqualTo(FORENAME + " " + OTHER_FORENAMES + " " + SURNAME);
        assertThat(name.getPersonTitleName()).isEqualTo(FORENAME + " " + OTHER_FORENAMES + " " + SURNAME);
        assertThat(name.getWildcardKey()).isEqualTo(SURNAME + " " + FORENAME + " " + OTHER_FORENAMES + "1");
    }

    @Test
    public void disqualifiedPersonNameWithNoOtherForenamesReturnsCorrectNames() {
        DisqualifiedPersonName name = new DisqualifiedPersonName(TITLE, FORENAME, "", SURNAME);

        assertThat(name.getPersonName()).isEqualTo(FORENAME + " " + SURNAME);
        assertThat(name.getPersonTitleName()).isEqualTo(TITLE + " " + FORENAME + " " + SURNAME);
        assertThat(name.getWildcardKey()).isEqualTo(SURNAME + " " + FORENAME + "1");
    }
}

package uk.gov.companieshouse.disqualifiedofficers.search.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DisqualifiedPersonName {

    private String title;
    private String forename;
    private String otherForenames;
    private String surname;

    public DisqualifiedPersonName(String title, String forename, String otherForenames, String surname) {
        this.title = title;
        this.forename = forename;
        this.otherForenames = otherForenames;
        this.surname = surname;
    }

    public String getPersonName() {
        return groupNames(forename, otherForenames, surname);
    }

    public String getPersonTitleName() {
        return groupNames(title, forename, otherForenames, surname);
    }

    public String getWildcardKey() {
        return groupNames(surname, forename, otherForenames) + "1";
    }

    private String groupNames(String... nameParts) {
        return Stream.of(nameParts).filter(s -> s!= null && !s.isEmpty()).collect(Collectors.joining(" "));
    }
}

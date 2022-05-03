package uk.gov.companieshouse.disqualifiedofficers.search.model;

public class CompanyName {

    private String name;
    private String ending;

    public CompanyName(String name, String ending) {
        this.name = name;
        this.ending = ending;
    }

    public String getName() {
        return name;
    }

    public String getEnding() {
        return ending;
    }
}

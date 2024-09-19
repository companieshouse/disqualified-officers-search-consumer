package uk.gov.companieshouse.disqualifiedofficers.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.disqualification.Disqualification;
import uk.gov.companieshouse.api.disqualification.DisqualificationLinks;
import uk.gov.companieshouse.api.disqualification.PermissionToAct;

import java.util.List;

public class StreamData {

    // Common properties
    @JsonProperty("disqualifications")
    private List<Disqualification> disqualifications;

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("permissions_to_act")
    private List<PermissionToAct> permissionsToAct;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("links")
    private DisqualificationLinks links;

    // Natural properties
    @JsonProperty("date_of_birth")
    private String dateOfBirth;

    @JsonProperty("forename")
    private String forename;

    @JsonProperty("honours")
    private String honours;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("other_forenames")
    private String otherForenames;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("title")
    private String title;

    @JsonProperty("person_number")
    private String personNumber;

    // Corporate properties
    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("country_of_registration")
    private String countryOfRegistraition;

    @JsonProperty("name")
    private String name;

    public List<Disqualification> getDisqualifications() {
        return disqualifications;
    }

    public String getEtag() {
        return etag;
    }

    public List<PermissionToAct> getPermissionsToAct() {
        return permissionsToAct;
    }

    public String getKind() {
        return kind;
    }

    public DisqualificationLinks getLinks() {
        return links;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getForename() {
        return forename;
    }

    public String getHonours() {
        return honours;
    }

    public String getNationality() {
        return nationality;
    }

    public String getOtherForenames() {
        return otherForenames;
    }

    public String getSurname() {
        return surname;
    }

    public String getTitle() {
        return title;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getCountryOfRegistraition() {
        return countryOfRegistraition;
    }

    public String getName() {
        return name;
    }

    public void setDisqualifications(List<Disqualification> disqualifications) {
        this.disqualifications = disqualifications;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setPermissionsToAct(List<PermissionToAct> permissionsToAct) {
        this.permissionsToAct = permissionsToAct;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setLinks(DisqualificationLinks links) {
        this.links = links;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public void setHonours(String honours) {
        this.honours = honours;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setOtherForenames(String otherForenames) {
        this.otherForenames = otherForenames;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public void setCountryOfRegistraition(String countryOfRegistraition) {
        this.countryOfRegistraition = countryOfRegistraition;
    }

    public void setName(String name) {
        this.name = name;
    }
}

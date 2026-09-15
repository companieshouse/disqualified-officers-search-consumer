package uk.gov.companieshouse.disqualifiedofficers.search.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.disqualification.Disqualification;
import uk.gov.companieshouse.api.disqualification.DisqualificationLinks;
import uk.gov.companieshouse.api.disqualification.PermissionToAct;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class StreamDataTest {

    private static final String ETAG = "etag-001";
    private static final String KIND = "natural-disqualification";
    private static final String DATE_OF_BIRTH = "1980-01-01";
    private static final String FORENAME = "John";
    private static final String HONOURS = "OBE";
    private static final String NATIONALITY = "British";
    private static final String OTHER_FORENAMES = "James";
    private static final String SURNAME = "Doe";
    private static final String TITLE = "Mr";
    private static final String PERSON_NUMBER = "P12345";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String COUNTRY_OF_REGISTRATION = "United Kingdom";
    private static final String NAME = "Doe Enterprises Ltd";

    @Mock
    private Disqualification disqualification;

    @Mock
    private PermissionToAct permissionToAct;

    @Mock
    private DisqualificationLinks disqualificationLinks;

    private StreamData streamData;

    @BeforeEach
    void setUp() {
        streamData = new StreamData();
    }

    @Test
    void shouldReturnNullWhenNoPropertiesSet() {
        assertNull(streamData.getDisqualifications());
        assertNull(streamData.getEtag());
        assertNull(streamData.getPermissionsToAct());
        assertNull(streamData.getKind());
        assertNull(streamData.getLinks());
        assertNull(streamData.getDateOfBirth());
        assertNull(streamData.getForename());
        assertNull(streamData.getHonours());
        assertNull(streamData.getNationality());
        assertNull(streamData.getOtherForenames());
        assertNull(streamData.getSurname());
        assertNull(streamData.getTitle());
        assertNull(streamData.getPersonNumber());
        assertNull(streamData.getCompanyNumber());
        assertNull(streamData.getCountryOfRegistraition());
        assertNull(streamData.getName());
    }

    @Test
    void shouldSetAndGetDisqualifications() {
        // Arrange
        List<Disqualification> disqualifications = List.of(disqualification);

        // Act
        streamData.setDisqualifications(disqualifications);

        // Assert
        assertEquals(disqualifications, streamData.getDisqualifications());
    }

    @Test
    void shouldSetAndGetEtag() {
        // Act
        streamData.setEtag(ETAG);

        // Assert
        assertEquals(ETAG, streamData.getEtag());
    }

    @Test
    void shouldSetAndGetPermissionsToAct() {
        // Arrange
        List<PermissionToAct> permissionsToAct = List.of(permissionToAct);

        // Act
        streamData.setPermissionsToAct(permissionsToAct);

        // Assert
        assertEquals(permissionsToAct, streamData.getPermissionsToAct());
    }

    @Test
    void shouldSetAndGetKind() {
        // Act
        streamData.setKind(KIND);

        // Assert
        assertEquals(KIND, streamData.getKind());
    }

    @Test
    void shouldSetAndGetLinks() {
        // Act
        streamData.setLinks(disqualificationLinks);

        // Assert
        assertEquals(disqualificationLinks, streamData.getLinks());
    }

    @Test
    void shouldSetAndGetDateOfBirth() {
        // Act
        streamData.setDateOfBirth(DATE_OF_BIRTH);

        // Assert
        assertEquals(DATE_OF_BIRTH, streamData.getDateOfBirth());
    }

    @Test
    void shouldSetAndGetForename() {
        // Act
        streamData.setForename(FORENAME);

        // Assert
        assertEquals(FORENAME, streamData.getForename());
    }

    @Test
    void shouldSetAndGetHonours() {
        // Act
        streamData.setHonours(HONOURS);

        // Assert
        assertEquals(HONOURS, streamData.getHonours());
    }

    @Test
    void shouldSetAndGetNationality() {
        // Act
        streamData.setNationality(NATIONALITY);

        // Assert
        assertEquals(NATIONALITY, streamData.getNationality());
    }

    @Test
    void shouldSetAndGetOtherForenames() {
        // Act
        streamData.setOtherForenames(OTHER_FORENAMES);

        // Assert
        assertEquals(OTHER_FORENAMES, streamData.getOtherForenames());
    }

    @Test
    void shouldSetAndGetSurname() {
        // Act
        streamData.setSurname(SURNAME);

        // Assert
        assertEquals(SURNAME, streamData.getSurname());
    }

    @Test
    void shouldSetAndGetTitle() {
        // Act
        streamData.setTitle(TITLE);

        // Assert
        assertEquals(TITLE, streamData.getTitle());
    }

    @Test
    void shouldSetAndGetPersonNumber() {
        // Act
        streamData.setPersonNumber(PERSON_NUMBER);

        // Assert
        assertEquals(PERSON_NUMBER, streamData.getPersonNumber());
    }

    @Test
    void shouldSetAndGetCompanyNumber() {
        // Act
        streamData.setCompanyNumber(COMPANY_NUMBER);

        // Assert
        assertEquals(COMPANY_NUMBER, streamData.getCompanyNumber());
    }

    @Test
    void shouldSetAndGetCountryOfRegistration() {
        // Act
        streamData.setCountryOfRegistraition(COUNTRY_OF_REGISTRATION);

        // Assert
        assertEquals(COUNTRY_OF_REGISTRATION, streamData.getCountryOfRegistraition());
    }

    @Test
    void shouldSetAndGetName() {
        // Act
        streamData.setName(NAME);

        // Assert
        assertEquals(NAME, streamData.getName());
    }

    @Test
    void shouldSetAndGetMultipleDisqualifications() {
        // Arrange
        Disqualification secondDisqualification = org.mockito.Mockito.mock(Disqualification.class);
        List<Disqualification> disqualifications = List.of(disqualification, secondDisqualification);

        // Act
        streamData.setDisqualifications(disqualifications);

        // Assert
        assertEquals(2, streamData.getDisqualifications().size());
        assertEquals(disqualifications, streamData.getDisqualifications());
    }

    @Test
    void shouldSetAndGetMultiplePermissionsToAct() {
        // Arrange
        PermissionToAct secondPermissionToAct = org.mockito.Mockito.mock(PermissionToAct.class);
        List<PermissionToAct> permissionsToAct = List.of(permissionToAct, secondPermissionToAct);

        // Act
        streamData.setPermissionsToAct(permissionsToAct);

        // Assert
        assertEquals(2, streamData.getPermissionsToAct().size());
        assertEquals(permissionsToAct, streamData.getPermissionsToAct());
    }
}
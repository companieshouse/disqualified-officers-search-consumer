package uk.gov.companieshouse.disqualifiedofficers.search.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CompanyNameTest {

    private static final String NAME = "Doe Enterprises";
    private static final String ENDING = "Ltd";

    @Test
    void shouldReturnNameWhenSet() {
        // Arrange
        CompanyName companyName = new CompanyName(NAME, ENDING);

        // Act & Assert
        assertEquals(NAME, companyName.getName());
    }

    @Test
    void shouldReturnEndingWhenSet() {
        // Arrange
        CompanyName companyName = new CompanyName(NAME, ENDING);

        // Act & Assert
        assertEquals(ENDING, companyName.getEnding());
    }

    @Test
    void shouldReturnNullWhenNameIsNull() {
        // Arrange
        CompanyName companyName = new CompanyName(null, ENDING);

        // Act & Assert
        assertNull(companyName.getName());
    }

    @Test
    void shouldReturnNullWhenEndingIsNull() {
        // Arrange
        CompanyName companyName = new CompanyName(NAME, null);

        // Act & Assert
        assertNull(companyName.getEnding());
    }

    @Test
    void shouldReturnNullWhenBothFieldsAreNull() {
        // Arrange
        CompanyName companyName = new CompanyName(null, null);

        // Act & Assert
        assertNull(companyName.getName());
        assertNull(companyName.getEnding());
    }

    @Test
    void shouldReturnEmptyStringWhenNameIsEmpty() {
        // Arrange
        CompanyName companyName = new CompanyName("", ENDING);

        // Act & Assert
        assertEquals("", companyName.getName());
    }

    @Test
    void shouldReturnEmptyStringWhenEndingIsEmpty() {
        // Arrange
        CompanyName companyName = new CompanyName(NAME, "");

        // Act & Assert
        assertEquals("", companyName.getEnding());
    }
}
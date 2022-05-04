package uk.gov.companieshouse.disqualifiedofficers.search.exception;

public class NonRetryableErrorException extends RuntimeException {
    public NonRetryableErrorException(String message) {
        super(message);
    }
}
package uk.gov.companieshouse.disqualifiedofficers.search.exception;

public class NonRetryableErrorException extends RuntimeException {
    public NonRetryableErrorException(Exception exception) {
        super(exception);
    }

    public NonRetryableErrorException(String message, Exception exception) {
        super(message, exception);
    }
}
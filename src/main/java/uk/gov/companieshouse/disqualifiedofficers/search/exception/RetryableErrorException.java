package uk.gov.companieshouse.disqualifiedofficers.search.exception;

public class RetryableErrorException extends RuntimeException {
    public RetryableErrorException(String message) {
        super(message);
    }
}
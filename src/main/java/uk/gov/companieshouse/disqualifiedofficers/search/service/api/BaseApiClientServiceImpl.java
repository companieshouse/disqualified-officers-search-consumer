package uk.gov.companieshouse.disqualifiedofficers.search.service.api;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.Executor;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.disqualifiedofficers.search.exception.NonRetryableErrorException;
import uk.gov.companieshouse.disqualifiedofficers.search.exception.RetryableErrorException;
import uk.gov.companieshouse.logging.Logger;

public abstract class BaseApiClientServiceImpl {

    protected Logger logger;

    protected BaseApiClientServiceImpl(Logger logger) {
        this.logger = logger;
    }

    /**
     * General execution of an SDK endpoint.
     *
     * @param <T>           type of api response
     * @param logContext    context ID for logging
     * @param operationName name of operation
     * @param uri           uri of sdk being called
     * @param executor      executor to use
     * @return the response object
     */
    public <T> ApiResponse<T> executeOp(final String logContext,
            final String operationName,
            final String uri,
            final Executor<ApiResponse<T>> executor) {

        final Map<String, Object> logMap = new HashMap<>();
        logMap.put("operation_name", operationName);
        logMap.put("path", uri);

        try {

            return executor.execute();

        } catch (URIValidationException ex) {
            String msg = "404 NOT_FOUND response received from search api, retry";
            logger.errorContext(logContext, msg, ex, logMap);

            throw new RetryableErrorException(msg, ex);
        } catch (ApiErrorResponseException ex) {
            int statusCode = ex.getStatusCode();
            logMap.put("status", statusCode);

            if (statusCode == HttpStatus.BAD_REQUEST.value()
                    || statusCode == HttpStatus.CONFLICT.value()) {
                String msg = String.format("%s response received from search api", statusCode);
                logger.errorContext(logContext, msg, ex, logMap);
                throw new NonRetryableErrorException(msg, ex);
            }

            String msg = String.format("%s response received from search api, retry", statusCode);
            logger.infoContext(logContext, msg, logMap);
            throw new RetryableErrorException(msg, ex);
        }
    }
}

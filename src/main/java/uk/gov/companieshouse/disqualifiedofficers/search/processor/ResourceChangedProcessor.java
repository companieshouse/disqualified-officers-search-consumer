package uk.gov.companieshouse.disqualifiedofficers.search.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.disqualifiedofficers.search.exception.RetryableErrorException;
import uk.gov.companieshouse.disqualifiedofficers.search.handler.ApiResponseHandler;
import uk.gov.companieshouse.disqualifiedofficers.search.service.api.ApiClientService;
import uk.gov.companieshouse.disqualifiedofficers.search.transformer.ElasticSearchTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.ResourceChangedData;

@Component
public class ResourceChangedProcessor {

    private final ElasticSearchTransformer transformer;
    private final ApiClientService apiService;
    private final Logger logger;

    /**
     * Constructor for the changed resource processor.
     * @param transformer transforms the incoming payload message to an elastic search object
     * @param logger logs out messages to the app logs
     * @param apiService handles PUT request to the search api
     */
    @Autowired
    public ResourceChangedProcessor(ElasticSearchTransformer transformer, Logger logger, 
            ApiClientService apiService) {
        this.transformer = transformer;
        this.apiService = apiService;
        this.logger = logger;
    }

    public void processResourceChanged(Message<ResourceChangedData> message) {
            final Map<String, Object> logMap = new HashMap<>();
            ResourceChangedData payload = message.getPayload();
            final String logContext = payload.getContextId();


            OfficerDisqualification elasticSearchData = transformer
                    .getOfficerDisqualificationFromResourceChanged(payload);

            String officerId = Stream.of( elasticSearchData.getLinks().getSelf().split("/") )
                    .reduce( (first,last) -> last ).get();

        try {
            final ApiResponse<Void> response =
                    apiService.putDisqualificationSearch(logContext, officerId, elasticSearchData);
            logger.infoContext(
                    logContext,
                    String.format("Process disqualification for officer with id [%s]", officerId),
                    null);

            ApiResponseHandler apiResponseHandler = new ApiResponseHandler();
            apiResponseHandler.handleResponse(null, HttpStatus.valueOf(response.getStatusCode()),
                    logContext,"Response received from search api", logMap, logger);
        } catch (RetryableErrorException ex) {
            retryResourceChangedMessage(message);
        } catch (Exception ex) {
            handleErrorMessage(message);
            // send to error topic
        }
    }

    public void retryResourceChangedMessage(Message<ResourceChangedData> message) {
    }

    private void handleErrorMessage(Message<ResourceChangedData> message) {
    }

}

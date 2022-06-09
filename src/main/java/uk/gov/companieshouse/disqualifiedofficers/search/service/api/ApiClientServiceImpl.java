package uk.gov.companieshouse.disqualifiedofficers.search.service.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;
import uk.gov.companieshouse.api.http.HttpClient;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.logging.Logger;


/**
 * Service that sends REST requests via private SDK.
 */
@Primary
@Service
public class ApiClientServiceImpl extends BaseApiClientServiceImpl implements ApiClientService {

    @Value("${api.search-api-key}")
    private String chsApiKey;

    @Value("${api.api-url}")
    private String apiUrl;

    @Value("${api.internal-api-url}")
    private String internalApiUrl;

    /**
     * Construct an {@link ApiClientServiceImpl}.
     *
     * @param logger the CH logger
     */
    @Autowired
    public ApiClientServiceImpl(final Logger logger) {
        super(logger);
    }

    @Override
    public InternalApiClient getApiClient(String contextId) {
        InternalApiClient internalApiClient = new InternalApiClient(getHttpClient(contextId));
        internalApiClient.setBasePath(apiUrl);
        internalApiClient.setInternalBasePath(internalApiUrl);
        return internalApiClient;
    }

    private HttpClient getHttpClient(String contextId) {
        ApiKeyHttpClient httpClient = new ApiKeyHttpClient(chsApiKey);
        httpClient.setRequestId(contextId);
        return httpClient;
    }

    @Override
    public ApiResponse<Void> putDisqualificationSearch(final String log, String officerId,
            OfficerDisqualification officerDisqualification) {
        final String uri = String.format("/disqualified-search/disqualified-officers/%s", officerId);

        Map<String, Object> logMap = createLogMap(officerId, "PUT", uri);
        logger.infoContext(log, String.format("PUT %s", uri), logMap);

        return executeOp(log, "putDisqualificationSearch", uri,
                getApiClient(log).privateSearchResourceHandler()
                        .putSearchDisqualification()
                        .upsert(uri, officerDisqualification));
    }

    @Override
    public ApiResponse<Void> deleteDisqualificationSearch(final String log, String officerId) {
        final String uri = String.format("/disqualified-search/delete/%s", officerId);

        Map<String, Object> logMap = createLogMap(officerId, "DELETE", uri);
        logger.infoContext(log, String.format("DELETE %s", uri), logMap);

        return executeOp(log, "deleteDisqualificationSearch", uri,
                getApiClient(log).privateSearchResourceHandler()
                        .deleteSearchDisqualification()
                        .delete(uri));
    }

    private Map<String, Object> createLogMap(String officerId, String method, String path) {
        final Map<String, Object> logMap = new HashMap<>();
        logMap.put("officer_id", officerId);
        logMap.put("method", method);
        logMap.put("path", path);
        return logMap;
    }
}
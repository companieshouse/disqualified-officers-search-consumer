package uk.gov.companieshouse.disqualifiedofficers.search.service.api;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.api.model.ApiResponse;

/**
 * The {@code ApiClientService} interface provides an abstraction that can be
 * used when testing {@code ApiClientManager} static methods, without imposing
 * the use of a test framework that supports mocking of static methods.
 */
public interface ApiClientService {

    InternalApiClient getApiClient(String contextId);

    /**
     * Submit disqualification.
     */
    ApiResponse<Void> putDisqualificationSearch(
            final String log,
            final String officerId,
            final OfficerDisqualification officerDisqualification);

    /**
     * Delete disqualification.
     */
    ApiResponse<Void> deleteDisqualificationSearch(
            final String log,
            final String officerId);
}
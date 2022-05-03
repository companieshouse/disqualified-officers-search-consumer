package uk.gov.companieshouse.disqualifiedofficers.search.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.disqualifiedofficers.search.transformer.ElasticSearchTransformer;
import uk.gov.companieshouse.stream.ResourceChangedData;

@Component
public class ResourceChangedProcessor {

    @Autowired
    private ElasticSearchTransformer transformer;

    public void processResourceChanged(Message<ResourceChangedData> message) {
        ResourceChangedData payload = message.getPayload();

        OfficerDisqualification elasticSearchData = transformer
                .getOfficerDisqualificationFromResourceChanged(payload);

        //apiService.sendElasticSearchData(elasticSearchData);
    }
}

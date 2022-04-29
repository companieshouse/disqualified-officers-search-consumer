package uk.gov.companieshouse.disqualifiedofficers.search.processor;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.stream.ResourceChangedData;

@Component
public class ResourceChangedProcessor {

    public void processResourceChanged(Message<ResourceChangedData> message) {
        ResourceChangedData payload = message.getPayload();

        Object elasticSearchData = transformer.getDataFromPayload(payload);

        apiService.sendElasticSearchData(elasticSearchData);
    }
}

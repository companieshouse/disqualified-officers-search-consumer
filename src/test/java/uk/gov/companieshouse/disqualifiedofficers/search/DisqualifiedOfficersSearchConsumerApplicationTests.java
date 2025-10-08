package uk.gov.companieshouse.disqualifiedofficers.search;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import uk.gov.companieshouse.disqualifiedofficers.search.config.LoggingConfig;

@SpringBootTest
class DisqualifiedOfficersSearchConsumerApplicationTests {

    @Test
    void contextLoads() {
        assertNotNull(LoggingConfig.getLogger());
    }

}

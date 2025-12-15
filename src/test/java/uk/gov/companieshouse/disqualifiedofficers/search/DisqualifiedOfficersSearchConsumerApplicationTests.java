package uk.gov.companieshouse.disqualifiedofficers.search;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import uk.gov.companieshouse.logging.Logger;

@SpringBootTest
class DisqualifiedOfficersSearchConsumerApplicationTests {

    @Autowired
    Logger logger;
    
    @Test
    void contextLoads() {
        assertNotNull(logger);
    }

}

package uk.gov.companieshouse.disqualifiedofficers.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.logging.Logger;

@RestController
public class HealthCheckController {

    @Autowired
    private Logger logger;

    @GetMapping("/healthcheck")
    public ResponseEntity<Void> healthcheck() {
        logger.info("Healthcheck endpoint called");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
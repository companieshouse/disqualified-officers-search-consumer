package uk.gov.companieshouse.disqualifiedofficers.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Configuration class for logging.
 */
@Configuration
public class LoggingConfig {

    /**
     * Main application logger with component specific namespace.
     *
     * @return the {@link LoggerFactory} for the specified namespace
     */
    @Bean
    public Logger logger(@Value("${logger.namespace}") String loggerNamespace) {
        return LoggerFactory.getLogger(loggerNamespace);
    }

}
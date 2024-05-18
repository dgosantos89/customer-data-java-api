package org.example.services.validate;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import org.example.domain.payloads.CreateConsent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

public class ExpirationDateValidator implements ConsentValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ExpirationDateValidator.class);
    @Override
    public void validate(CreateConsent request) {
        LOG.info("Validating the expirationDateTime");
        if (request.getData().getExpirationDateTime().isAfter(OffsetDateTime.now().plusYears(1l))) {
            LOG.info("expirationDateTime {} not valid", request.getData().getExpirationDateTime());
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "ExpirationDateTime can't be further than a year");
        }
    }
}

package org.example.services.validate;

import org.example.domain.payloads.CreateConsent;

public interface ConsentValidator {

    void validate(CreateConsent request);

}

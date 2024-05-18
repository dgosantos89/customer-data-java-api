package org.example.services;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.example.domain.entities.Consent;
import org.example.domain.entities.EnumConsentStatus;
import org.example.domain.payloads.CreateConsent;
import org.example.domain.payloads.UpdateConsent;
import org.example.domain.responses.ResponseConsent;
import org.example.repositories.ConsentRepository;
import org.example.services.validate.ConsentValidator;
import org.example.services.validate.ExpirationDateValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.*;

@Singleton
@Transactional
public class ConsentService {

    @Inject
    ConsentRepository consentRepository;

    private final List<ConsentValidator> validators = List.of(
            new ExpirationDateValidator());

    public ResponseConsent createConsent(String clientId, @NotNull CreateConsent body) {
        validateRequest(body);

        Consent consent = consentRepository.save(Consent.fromRequest(body, clientId));
        return consentRepository.findById(consent.getId())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Could not find recently saved ConsentEntity"))
                .toResponseConsent();
    }

    public ResponseConsent getConsent(String consentId, String clientId) {
        Consent entity = consentRepository.findById(consentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Consent Id " + consentId + " not found"));

        if (!entity.getClientId().equals(clientId)) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Consent belongs to another client");
        }

        return entity.toResponseConsent();
    }

    public ResponseConsent updateConsent(@NotNull String consentId, UpdateConsent request) {
        Consent consent = updateConsentEntity(consentId, request);
        return consent.toResponseConsent();
    }

    private Consent updateConsentEntity(String consentId, UpdateConsent request) {
        if (request.getData().getStatus() == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Request data missing a status value");
        }

        if (!request.getData().getStatus().equals(EnumConsentStatus.REJECTED.name()) && !request.getData().getStatus().equals(EnumConsentStatus.AUTHORISED.name())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Status not allowed");
        }

        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Consent Id " + consentId + " not found"));

        if (consent.getStatus().equals(EnumConsentStatus.REJECTED)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Consent already Rejected");
        }

        if (consent.getExpirationDateTime().isBefore((LocalDateTime.now()))) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Consent expired");
        }

        consent.setStatus(EnumConsentStatus.valueOf(request.getData().getStatus()));
        consent.setStatusUpdateDateTime(LocalDateTime.now());
        consentRepository.update(consent);

        return consentRepository.findById(consent.getId())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not look up consent that has just been updated"));
    }

    private void validateRequest(CreateConsent body) {
        validators.forEach(v -> v.validate(body));
    }

}

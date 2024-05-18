package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import org.example.domain.payloads.CreateConsent;
import org.example.domain.payloads.UpdateConsent;
import org.example.domain.responses.ResponseConsent;
import org.example.services.ConsentService;
import org.example.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;

@RolesAllowed({"CONSENTS_MANAGE"})
@Controller("/test-api/consents")
public class ConsentController {
    private static final Logger LOG = LoggerFactory.getLogger(ConsentController.class);
    private final ConsentService service;

    @Value("${test-api.max-page-size}")
    int maxPageSize;

    @Value("${test-api.baseUrl}")
    protected String appBaseUrl;

    @Inject
    protected ObjectMapper mapper;

    ConsentController(ConsentService service) {
        this.service = service;
    }

    @Post("/v1/consents")
    @Status(HttpStatus.CREATED)
    public ResponseConsent createConsentV2(@Body @Valid CreateConsent body, HttpRequest<?> request) {
        var clientId = Utils.getRequestClientId(request);
        LOG.info("Creating new consent for client {}", clientId);
        Utils.logObject(mapper, body);
        ResponseConsent consentResponse = service.createConsent(clientId, body);
        String consentId = consentResponse.getData().getConsentId();
        Utils.decorateResponse(consentResponse::setLinks, consentResponse::setMeta, appBaseUrl + request.getPath() + "/" + consentId, 1, maxPageSize);
        LOG.info("Consent created");
        Utils.logObject(mapper, consentResponse);
        return consentResponse;
    }

    @Get("/v1/consents/{consentId}")
    public Object getConsent(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Looking up consent {}", consentId);
        var clientId = Utils.getRequestClientId(request);
        var consentResponse = service.getConsent(consentId, clientId);
        Utils.decorateResponse(consentResponse::setLinks, consentResponse::setMeta, appBaseUrl + request.getPath() + "/" + consentId, 1, maxPageSize);
        LOG.info("Returning consent data");
        Utils.logObject(mapper, consentResponse);
        return consentResponse;
    }

    @Put("/v1/consents/{consentId}")
    public ResponseConsent putConsent(@PathVariable("consentId") String consentId, @Body @Valid UpdateConsent request) {
        LOG.info("Updating consent {}", consentId);
        Utils.logObject(mapper, request);
        var response = service.updateConsent(consentId, request);
        Utils.logObject(mapper, response);
        return response;
    }
}

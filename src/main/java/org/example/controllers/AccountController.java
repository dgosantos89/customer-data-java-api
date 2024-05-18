package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import org.example.domain.responses.ResponseListAccount;
import org.example.services.AccountService;
import org.example.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@RolesAllowed({"ACCOUNTS_READ"})
@Controller("/test-api/account")
public class AccountController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);
    private final AccountService service;

    @Value("${test-api.max-page-size}")
    int maxPageSize;

    @Value("${test-api.baseUrl}")
    protected String appBaseUrl;

    @Inject
    protected ObjectMapper mapper;

    AccountController(AccountService service) {
        this.service = service;
    }

    @Get("/v1/accounts")
    public Object getAccounts(HttpRequest<?> request) {
        LOG.info("Looking up all accounts}");
        var consentId = Utils.getRequestConsentId(request);
        var pageableResponse = service.getAccounts(Pageable.from(0), consentId);
        var response = new ResponseListAccount().data(pageableResponse.getContent());
        Utils.decorateResponse(response::setLinks, response::setMeta, appBaseUrl + request.getPath(), pageableResponse.getTotalSize(), maxPageSize);
        LOG.info("Returning all accounts found");
        Utils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/account/{accountId}")
    public Object getAccountById(@PathVariable("accountId") String accountId, HttpRequest<?> request) {
        LOG.info("Looking up account {}", accountId);
        var consentId = Utils.getRequestConsentId(request);
        var consentResponse = service.getAccount(UUID.fromString(accountId), consentId);
        Utils.decorateResponse(consentResponse::setLinks, consentResponse::setMeta, appBaseUrl + request.getPath() + "/" + accountId, 1, maxPageSize);
        LOG.info("External client making call - return partial response");
        Utils.logObject(mapper, consentResponse);
        return consentResponse;
    }

}

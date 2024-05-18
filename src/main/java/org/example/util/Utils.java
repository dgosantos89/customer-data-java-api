package org.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import org.example.domain.entities.Consent;
import org.example.domain.entities.EnumConsentPermissions;
import org.example.domain.entities.EnumConsentStatus;
import org.example.domain.responses.Links;
import org.example.domain.responses.Meta;
import org.example.repositories.ConsentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.function.Consumer;

public class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static void decorateResponse(Consumer<Links> setLinks, Consumer<Meta> setMeta, String self, long records, int pageSize) {
        setLinks.accept(new Links().self(self));
        var page = pageSize <= 0 ? 0 : (int) Math.ceil((double) records / pageSize);
        setMeta.accept(new Meta().totalPages(page).totalRecords(records).requestDateTime(OffsetDateTime.now()));
    }

    public static void logObject(ObjectMapper mapper, Object res) {
        try {
            String response = mapper.writeValueAsString(res);
            LOG.info("{} - {}", res.getClass().getSimpleName(), response);
        } catch (JsonProcessingException e) {
            LOG.error("{} - Error writing object as JSON: ", res.getClass().getSimpleName(), e);
        }
    }

    public static void validateConsent(String consentId, ConsentRepository repository, EnumConsentPermissions permissions) {
        if (consentId == null) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Consent Id not present on the request");
        }
        Consent entity = repository.findById(consentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Consent Id " + consentId + " not found"));

        if (!entity.getConsentPermissions().equals(permissions)) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Consent Id " + consentId + " doesn't have right permissions");
        }

        if (!entity.getStatus().equals(EnumConsentStatus.AUTHORISED)) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Consent Id " + consentId + " is not in the right status");
        }
    }

    public static String getRequestClientId(HttpRequest<?> request) {
        String clientId = request.getAttribute("clientId")
                .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST, "Access token did not contain a client ID")).toString();
        LOG.info("Request made by client id: {}", clientId);
        return clientId;
    }

    public static String getRequestConsentId(HttpRequest<?> request) {
        String consentId = request.getAttribute("consentId").map(Object::toString).orElse(null);
        LOG.info("Request made with consent Id: {}", consentId);
        return consentId;
    }
}

package org.example.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ResponseConsentData {
    @JsonProperty("consentId")
    private String consentId;

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("permission")
    private String permission;

    @JsonProperty("expirationDatTime")
    private String expirationDateTime;

    @JsonProperty("creationDateTime")
    private String creationDateTime;

    @JsonProperty("statusUpdateDateTime")
    private String statusUpdateDateTime;


    public ResponseConsentData consentId(String consentId) {
        this.consentId = consentId;
        return this;
    }

    public ResponseConsentData status(String status) {
        this.status = status;
        return this;
    }

    public ResponseConsentData permission(String permission) {
        this.permission = permission;
        return this;
    }

    public ResponseConsentData clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ResponseConsentData expirationDateTime(LocalDateTime expirationDateTime) {
        this.expirationDateTime = expirationDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        return this;
    }

    public ResponseConsentData creationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        return this;
    }

    public ResponseConsentData statusUpdateDateTime(LocalDateTime statusUpdateDateTime) {
        this.statusUpdateDateTime = statusUpdateDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        return this;
    }

}

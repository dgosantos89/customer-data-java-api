package org.example.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseConsent {
    @JsonProperty("data")
    private ResponseConsentData data = null;
    @JsonProperty("links")
    private Links links = null;
    @JsonProperty("meta")
    private Meta meta = null;

    public ResponseConsent data(ResponseConsentData data) {
        this.data = data;
        return this;
    }

}

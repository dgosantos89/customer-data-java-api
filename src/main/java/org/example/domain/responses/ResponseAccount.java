package org.example.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseAccount {
    @JsonProperty("data")
    private ResponseAccountData data = null;
    @JsonProperty("links")
    private Links links = null;
    @JsonProperty("meta")
    private Meta meta = null;

    public ResponseAccount data(ResponseAccountData data) {
        this.data = data;
        return this;
    }

}

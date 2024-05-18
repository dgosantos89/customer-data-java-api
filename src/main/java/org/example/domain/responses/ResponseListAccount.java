package org.example.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseListAccount {
    @JsonProperty("data")
    private List<ResponseAccountData> data = new ArrayList<>();
    @JsonProperty("links")
    private Links links = null;
    @JsonProperty("meta")
    private Meta meta = null;

    public ResponseListAccount data(List<ResponseAccountData> data) {
        this.data.addAll(data);
        return this;
    }

}

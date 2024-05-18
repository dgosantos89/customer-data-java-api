package org.example.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Links {
    @JsonProperty("self")
    private String self = null;
    @JsonProperty("first")
    private String first = null;
    @JsonProperty("prev")
    private String prev = null;
    @JsonProperty("next")
    private String next = null;
    @JsonProperty("last")
    private String last = null;

    public Links self(String self) {
        this.self = self;
        return this;
    }
}
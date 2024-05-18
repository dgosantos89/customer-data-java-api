package org.example.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Meta {
    @JsonProperty("totalRecords")
    private Long totalRecords = null;
    @JsonProperty("totalPages")
    private Integer totalPages = null;
    @JsonProperty("requestDateTime")
    private OffsetDateTime requestDateTime = null;

    public Meta totalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
        return this;
    }

    public Meta totalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public Meta requestDateTime(OffsetDateTime requestDateTime) {
        this.requestDateTime = requestDateTime;
        return this;
    }
}

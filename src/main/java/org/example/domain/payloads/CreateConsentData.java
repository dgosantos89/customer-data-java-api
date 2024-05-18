package org.example.domain.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Introspected
public class CreateConsentData {

    @JsonProperty("permissions")
    private String permissions = null;

    @JsonProperty("expirationDateTime")
    private OffsetDateTime expirationDateTime = null;
}

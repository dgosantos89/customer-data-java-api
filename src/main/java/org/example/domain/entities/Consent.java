package org.example.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.example.domain.payloads.CreateConsent;
import org.example.domain.responses.ResponseConsent;
import org.example.domain.responses.ResponseConsentData;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Data
@Entity
@Table(name = "consents")
public class Consent {
    @Id
    @GenericGenerator(name = "string_based_custom_sequence", strategy = "org.example.domain.entities.CustomUserIdGenerator")
    @GeneratedValue(generator = "string_based_custom_sequence")
    private String id;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "status")
    @Convert(converter = ConsentStatusConverter.class)
    private EnumConsentStatus status;

    @Column(name = "creation_date_time")
    private LocalDateTime creationDateTime;

    @Column(name = "status_update_date_time")
    private LocalDateTime statusUpdateDateTime;

    @Column(name = "expiration_date_time")
    private LocalDateTime expirationDateTime;

    @Column(name = "consent_permission")
    @Convert(converter = ConsentPermissionsConverter.class)
    private EnumConsentPermissions consentPermissions;

    public static Consent fromRequest(CreateConsent body, String clientId) {
        var consentEntity = new Consent();
        consentEntity.setClientId(clientId);
        consentEntity.setExpirationDateTime(body.getData().getExpirationDateTime().toLocalDateTime());
        consentEntity.setConsentPermissions(EnumConsentPermissions.valueOf(body.getData().getPermissions()));
        consentEntity.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION);
        consentEntity.setCreationDateTime(LocalDateTime.now());
        consentEntity.setStatusUpdateDateTime(LocalDateTime.now());
        return consentEntity;
    }

    public ResponseConsent toResponseConsent() {
        return new ResponseConsent().data(new ResponseConsentData()
                .consentId(id)
                .clientId(clientId)
                .status(status.name())
                .permission(consentPermissions.name())
                .expirationDateTime(expirationDateTime)
                .creationDateTime(creationDateTime));
    }

    @Converter
    static class ConsentPermissionsConverter implements AttributeConverter<EnumConsentPermissions, String> {

        @Override
        public String convertToDatabaseColumn(EnumConsentPermissions attribute) {
            return Optional.ofNullable(attribute)
                    .map(EnumConsentPermissions::toString)
                    .orElse(null);
        }

        @Override
        public EnumConsentPermissions convertToEntityAttribute(String dbData) {
            return Optional.ofNullable(dbData)
                    .map(EnumConsentPermissions::valueOf)
                    .orElse(null);
        }
    }

    @Converter
    static class ConsentStatusConverter implements AttributeConverter<EnumConsentStatus, String> {

        @Override
        public String convertToDatabaseColumn(EnumConsentStatus attribute) {
            return Optional.ofNullable(attribute)
                    .map(EnumConsentStatus::toString)
                    .orElse(null);
        }

        @Override
        public EnumConsentStatus convertToEntityAttribute(String dbData) {
            return Optional.ofNullable(dbData)
                    .map(EnumConsentStatus::valueOf)
                    .orElse(null);
        }
    }
}

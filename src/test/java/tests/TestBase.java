package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;

public class TestBase {

    protected static String consentId;
    protected static String consentToken;

    @BeforeAll
    public static void setup() {
        // Set base URI
        RestAssured.baseURI = "http://localhost:8080/test-api";

        // Set the default timezone to GMT
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        // Generate JWT token with "consents" scope
        consentToken = generateConsentToken();

        // Create consent
        consentId = createAccountConsent(consentToken, LocalDateTime.now().plusDays(1));

        // Authorize consent
        updateConsentStatus(consentId, consentToken, "AUTHORISED");
    }

    // Utility methods for generating tokens
    protected static String generateJwtToken(String scope, String consentId) {
        String header = "{\"alg\": \"none\", \"typ\": \"JWT\"}";
        String payload = consentId != null
                ? "{\"scope\": \"" + scope + " consent:" + consentId + "\", \"client_id\": \"client1\"}"
                : "{\"scope\": \"" + scope + "\", \"client_id\": \"client1\"}";

        return encodeBase64(header) + "." + encodeBase64(payload) + ".";
    }

    protected static String generateConsentToken() {
        return generateJwtToken("consents", null);
    }

    protected static String generateTokenWithoutPayload() {
        String header = "{\"alg\": \"none\", \"typ\": \"JWT\"}";
        return encodeBase64(header) + "..";
    }

    protected static String generateTokenWithNonExistentConsent() {
        return generateJwtToken("accounts", "urn:bank:123");
    }

    protected static String generateTokenInvalidConsent() {
        return generateJwtToken("accounts", "123");
    }

    protected static String generateTokenAwaitingAuthorisation() {
        String newConsentId = createAccountConsent(consentToken, LocalDateTime.now().plusDays(1));
        return generateJwtToken("accounts", newConsentId);
    }

    protected static String generateRejectedToken() {
        String newConsentId = createAccountConsent(consentToken, LocalDateTime.now().plusDays(1));
        updateConsentStatus(newConsentId, consentToken, "REJECTED");
        return generateJwtToken("accounts", newConsentId);
    }

    private static String createAccountConsent(String token, LocalDateTime expirationDateTime) {
        String formattedExpirationTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(expirationDateTime);
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body("{\"data\": {\"permissions\": \"ACCOUNTS_READ\", \"expirationDateTime\": \"" + formattedExpirationTime + "\"}}")
                .when()
                .post("/consents/v1/consents")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        return response.path("data.consentId");
    }

    protected static void updateConsentStatus(String consentId, String token, String status) {
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body("{\"data\": {\"status\": \"" + status + "\"}}")
        .when()
                .put("/consents/v1/consents/" + consentId)
        .then()
                .statusCode(200);
    }

    private static String encodeBase64(String data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data.getBytes());
    }

    protected static String getValidToken() {
        return generateJwtToken("accounts", consentId);
    }

    protected String generateTokenWithExpiredConsent() {
        // Create a consent that expires soon
        String newConsentId = createAccountConsent(consentToken, LocalDateTime.now().plusSeconds(30));

        // Approve the consent
        updateConsentStatus(newConsentId, consentToken, "AUTHORISED");

        // Wait to expire
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Test interrupted while waiting for consent to expire", e);
        }

        // Return the token for the expired consent
        return generateJwtToken("accounts", newConsentId);
    }
}

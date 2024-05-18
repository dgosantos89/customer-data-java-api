package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import java.util.Base64;

import static io.restassured.RestAssured.given;

public class testBase {

    protected static String consentId;
    protected static String consentToken;

    @BeforeAll
    public static void setup() {
        // Set base URI
        RestAssured.baseURI = "http://localhost:8080/test-api";

        // Generate JWT token with "consents" scope
        consentToken = generateConsentToken();

        // Create consent
        consentId = createConsent(consentToken);

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

    protected static String generateInvalidToken() {
        return "invalid.token.value";
    }

    protected static String generateTokenWithoutPayload() {
        String header = "{\"alg\": \"none\", \"typ\": \"JWT\"}";
        return encodeBase64(header) + "..";
    }

    protected static String generateTokenWithNonExistentConsent() {
        return generateJwtToken("accounts", "123");
    }

    protected static String generateTokenAwaitingAuthorisation() {
        String newConsentId = createConsent(consentToken);
        return generateJwtToken("consents", newConsentId);
    }

    protected static String generateRejectedToken() {
        String newConsentId = createConsent(consentToken);
        updateConsentStatus(newConsentId, consentToken, "REJECTED");
        return generateJwtToken("accounts", newConsentId);
    }

    private static String createConsent(String token) {
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body("{\"data\": {\"permissions\": \"ACCOUNTS_READ\", \"expirationDateTime\": \"2024-12-21T13:54:31Z\"}}")
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
}

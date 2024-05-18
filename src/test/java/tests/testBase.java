package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import java.util.Base64;

import static io.restassured.RestAssured.given;

public class testBase {

    protected static String accessToken;
    protected static String consentId;

    @BeforeAll
    public static void setup() {
        // Set base URI
        RestAssured.baseURI = "http://localhost:8080/test-api";

        // Generate JWT token with "consents" scope
        accessToken = generateJwtToken("consents", null);

        // Create consent
        consentId = createConsent(accessToken);

        // Authorize consent
        authorizeConsent(consentId, accessToken);

        // Generate new JWT token with "accounts" scope and consent ID
        accessToken = generateJwtToken("accounts", consentId);
    }

    private static String generateJwtToken(String scope, String consentId) {
        String header = "{\"alg\": \"none\", \"typ\": \"JWT\"}";
        String payload;

        if (consentId != null) {
            payload = "{\"scope\": \"" + scope + " consent:" + consentId + "\", \"client_id\": \"client1\"}";
        } else {
            payload = "{\"scope\": \"" + scope + "\", \"client_id\": \"client1\"}";
        }

        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());

        return encodedHeader + "." + encodedPayload + ".";
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

    protected static void authorizeConsent(String consentId, String token) {
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body("{\"data\": {\"status\": \"AUTHORISED\"}}")
                .when()
                .put("/consents/v1/consents/" + consentId)
                .then()
                .statusCode(200);
    }
}

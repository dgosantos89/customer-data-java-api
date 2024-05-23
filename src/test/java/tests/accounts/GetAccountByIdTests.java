package tests.accounts;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class GetAccountByIdTests extends TestBase {

    private static final String ACCOUNT_ID_VALID = "87caf37b-f70f-440c-bacd-3b9399ca5d74";
    private static final String ACCOUNT_ENDPOINT = "/account/v1/account/";

    @Test
    // TC001 - Valid account ID returns account data
    public void testValidAccountIdReturnsAccountData() {
        given()
                .header("Authorization", "Bearer " + getValidToken())
        .when()
                .get(ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID)
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("data.id", equalTo(ACCOUNT_ID_VALID))
                .body("data.bank", equalTo("Nubank"))
                .body("data.accountNumero", equalTo("1234567-8"))
                .body("links.self", containsString(ACCOUNT_ID_VALID))
                .body("meta.totalRecords", equalTo(1))
                .body("meta.totalPages", equalTo(1))
                .body("meta.requestDateTime", notNullValue());
    }

    @Test
    // TC002 - Invalid account ID returns error
    public void testInvalidAccountIdReturnsError() {
        String invalidAccountId = "INVALID_ACCOUNT_ID";

        given()
                .header("Authorization", "Bearer " + getValidToken())
        .when()
                .get(ACCOUNT_ENDPOINT + invalidAccountId)
        .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Internal Server Error"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + invalidAccountId))
                .body("_embedded.errors[0].message", equalTo("Internal Server Error: Invalid UUID string: " + invalidAccountId))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    @Tag("bugs")
    // TC003 - A short account ID returns error
    public void testShortAccountIdReturnsError() {
        String shortAccountId = "87caf37b-f70f-440c-bacd-3b9399ca5d7";

        given()
                .header("Authorization", "Bearer " + getValidToken())
        .when()
                .get(ACCOUNT_ENDPOINT + shortAccountId)
        .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Not Found"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + shortAccountId))
                .body("_embedded.errors[0].message", equalTo("Internal Server Error: Invalid UUID string: " + shortAccountId))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    // TC004 - Account ID not found returns error
    public void testAccountIdNotFoundReturnsError(){
        String nonExistentAccount = "ab535c7a-a29a-4926-884d-beff58d50db3";

        given()
                .header("Authorization", "Bearer " + getValidToken())
        .when()
                .get(ACCOUNT_ENDPOINT + nonExistentAccount)
        .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Not Found"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + nonExistentAccount))
                .body("_embedded.errors[0].message", equalTo("Account Id " + nonExistentAccount + " not found"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());

    }

    @Test
    // TC005 - Request without proper authorization returns error
    public void testWithoutAuthorizationReturnsError(){
        when()
                .get(ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID)
        .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Unauthorized"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID))
                .body("_embedded.errors[0].message", equalTo("Unauthorized"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    // TC006 - Request with an invalid token returns error
    public void testWithInvalidTokenReturnsError() {
        String invalidToken = "INVALID_TOKEN";

        given()
                .header("Authorization", "Bearer " + invalidToken)
        .when()
                .get(ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID)
        .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Unauthorized"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID))
                .body("_embedded.errors[0].message", equalTo("Unauthorized"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    // TC007 - Request with token without payload returns error
    public void testWithInvalidTokenPayloadReturnsError() {
        String tokenWithoutPayload = generateTokenWithoutPayload();

        given()
                .header("Authorization", "Bearer " + tokenWithoutPayload)
        .when()
                .get(ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID)
        .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Internal Server Error"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID))
                .body("_embedded.errors[0].message", equalTo("Internal Server Error: Cannot invoke \"java.util.Map.getOrDefault(Object, Object)\" because \"payload\" is null"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    // TC008 - Request with a consent scope token returns error
    public void testWithConsentsScopeReturnsError() {
        String consentsToken = generateConsentToken();

        given()
                .header("Authorization", "Bearer " + consentsToken)
        .when()
                .get(ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID)
        .then()
                .statusCode(403)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Forbidden"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID))
                .body("_embedded.errors[0].message", equalTo("Forbidden"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    @Tag("bugs")
    // TC009 - Request with a non-existent consent ID scope token returns error
    public void testWithNonExistentConsentReturnsError() {
        String notExistentConsentToken = generateTokenWithNonExistentConsent();

        given()
                .header("Authorization", "Bearer " + notExistentConsentToken)
        .when()
                .get(ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID)
        .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Not Found"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID))
                .body("_embedded.errors[0].message", equalTo("Consent Id urn:bank:123 not found"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    @Tag("bugs")
    // TC010 - Request with a consentId in AWAITING_AUTHORISATION status returns error
    public void testWithConsentAwaitingAuthorisationReturnsError() {
        String awaitingAuthorisationToken = generateTokenAwaitingAuthorisation();

        given()
                .header("Authorization", "Bearer " + awaitingAuthorisationToken)
        .when()
                .get(ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID)
        .then()
                .statusCode(403)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Forbidden"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID))
                .body("_embedded.errors[0].message", containsString("is not in the right status"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    @Tag("bugs")
    // TC011 - Request with a consentId in REJECTED status returns error
    public void testWithConsentRejectedReturnsError() {
        String rejectedToken = generateRejectedToken();

        given()
                .header("Authorization", "Bearer " + rejectedToken)
        .when()
                .get(ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID)
        .then()
                .statusCode(403)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Forbidden"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID))
                .body("_embedded.errors[0].message", containsString("is not in the right status"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    @Tag("bugs")
    // TC012 - Request with an expired consentId returns error
    public void testWithExpiredConsentReturnsError() {
        // Make the request and verify the response
        given()
                .header("Authorization", "Bearer " + generateTokenWithExpiredConsent())
        .when()
                .get(ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID)
        .then()
                .statusCode(403)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Forbidden"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID))
                .body("_embedded.errors[0].message", equalTo("Consent expired"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    @Tag("bugs")
    // TC013 - Request without a valid consentId returns error
    public void testWithInvalidConsentReturnsError() {
        // Make the request and verify the response
        given()
                .header("Authorization", "Bearer " + generateTokenInvalidConsent())
        .when()
                .get(ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID)
        .then()
                .statusCode(403)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Forbidden"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNT_ENDPOINT + ACCOUNT_ID_VALID))
                .body("_embedded.errors[0].message", equalTo("Consent Id not present on the request"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }
}

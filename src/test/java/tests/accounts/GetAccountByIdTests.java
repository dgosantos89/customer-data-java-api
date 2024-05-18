package tests.accounts;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.testBase;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class GetAccountByIdTests extends testBase {

    @Test
    //TC001 - Valid account Id returns account data
    public void testValidAccountIdReturnsAccountDetails() {
        String accountId = "87caf37b-f70f-440c-bacd-3b9399ca5d74";

        given()
                .header("Authorization", "Bearer " + getValidToken())
        .when()
                .get("/account/v1/account/" + accountId)
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("data.id", equalTo(accountId))
                .body("data.bank", equalTo("Nubank"))
                .body("data.accountNumero", equalTo("1234567-8"))
                .body("links.self", containsString(accountId))
                .body("meta.totalRecords", equalTo(1))
                .body("meta.totalPages", equalTo(1))
                .body("meta.requestDateTime", notNullValue());
    }

    @Test
    //TC002 - Invalid account ID returns error message
    public void testInvalidAccountIdReturnsErrorMessage() {
        String invalidAccountId = "INVALID_ACCOUNT_ID";

        given()
                .header("Authorization", "Bearer " + getValidToken())
        .when()
                .get("/account/v1/account/" + invalidAccountId)
        .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Internal Server Error"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + invalidAccountId))
                .body("_embedded.errors[0].message", equalTo("Internal Server Error: Invalid UUID string: " + invalidAccountId))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    @Tag("bugs")
    //TC003 - A short account ID returns error message
    public void testShortAccountIdReturnsErrorMessage() {
        String shortAccountId = "87caf37b-f70f-440c-bacd-3b9399ca5d7";

        given()
                .header("Authorization", "Bearer " + getValidToken())
        .when()
                .get("/account/v1/account/" + shortAccountId)
        .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Not Found"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + shortAccountId))
                .body("_embedded.errors[0].message", equalTo("Internal Server Error: Invalid UUID string: " + shortAccountId))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    //TC004 - Account ID not found returns error message
    public void testAccountIdNotFoundReturnsErrorMessage(){
        String nonExistentAccount = "ab535c7a-a29a-4926-884d-beff58d50db3";

        given()
                .header("Authorization", "Bearer " + getValidToken())
        .when()
                .get("/account/v1/account/" + nonExistentAccount)
        .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Not Found"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + nonExistentAccount))
                .body("_embedded.errors[0].message", equalTo("Account Id " + nonExistentAccount + " not found"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());

    }

    @Test
    //TC005 - Request without proper authorization returns error
    public void testWithoutAuthorizationReturnsErrorMessage(){
        String accountId = "87caf37b-f70f-440c-bacd-3b9399ca5d74";

        when()
                .get("/account/v1/account/" + accountId)
        .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Unauthorized"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + accountId))
                .body("_embedded.errors[0].message", equalTo("Unauthorized"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    // TC006 - Request with an invalid token returns error
    public void testWithInvalidTokenReturnsErrorMessage() {
        String invalidToken = "INVALID_TOKEN";
        String accountId = "87caf37b-f70f-440c-bacd-3b9399ca5d74";

        given()
                .header("Authorization", "Bearer " + invalidToken)
        .when()
                .get("/account/v1/account/" + accountId)
        .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Unauthorized"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + accountId))
                .body("_embedded.errors[0].message", equalTo("Unauthorized"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    // TC007 - Request with token without payload
    public void testWithInvalidTokenPayloadReturnsErrorMessage() {
        String tokenWithoutPayload = generateTokenWithoutPayload();
        String accountId = "87caf37b-f70f-440c-bacd-3b9399ca5d74";

        given()
                .header("Authorization", "Bearer " + tokenWithoutPayload)
                .when()
                .get("/account/v1/account/" + accountId)
                .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Internal Server Error"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + accountId))
                .body("_embedded.errors[0].message", equalTo("Internal Server Error: Cannot invoke \"java.util.Map.getOrDefault(Object, Object)\" because \"payload\" is null"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    // TC008 - Request with a consent scope token
    public void testWithConsentsScopeReturnsErrorMessage() {
        String consentsToken = generateConsentToken();
        String accountId = "87caf37b-f70f-440c-bacd-3b9399ca5d74";

        given()
                .header("Authorization", "Bearer " + consentsToken)
                .when()
                .get("/account/v1/account/" + accountId)
                .then()
                .statusCode(403)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Forbidden"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + accountId))
                .body("_embedded.errors[0].message", equalTo("Forbidden"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    @Tag("bugs")
    // TC009 - Request with a not existent consent ID scope token
    public void testWithANotExistentConsentReturnsErrorMessage() {
        String notExistentConsentToken = generateTokenWithNonExistentConsent();
        String accountId = "87caf37b-f70f-440c-bacd-3b9399ca5d74";

        given()
                .header("Authorization", "Bearer " + notExistentConsentToken)
                .when()
                .get("/account/v1/account/" + accountId)
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Not Found"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + accountId))
                .body("_embedded.errors[0].message", equalTo("Consent Id 123 not found"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    @Tag("bugs")
    // TC010 - Request with a consentId in AWAITING_AUTHORISATION status
    public void testWithConsentAwaitingAuthorisationReturnsErrorMessage() {
        String notExistentConsentToken = generateTokenAwaitingAuthorisation();
        String accountId = "87caf37b-f70f-440c-bacd-3b9399ca5d74";

        given()
                .header("Authorization", "Bearer " + notExistentConsentToken)
                .when()
                .get("/account/v1/account/" + accountId)
                .then()
                .statusCode(403)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Forbidden"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + accountId))
                .body("_embedded.errors[0].message", containsString("is not in the right status"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    @Tag("bugs")
    // TC011 - Request with a consentId in REJECTED status
    public void testWithConsentRejectedReturnsErrorMessage() {
        String notExistentConsentToken = generateRejectedToken();
        String accountId = "87caf37b-f70f-440c-bacd-3b9399ca5d74";

        given()
                .header("Authorization", "Bearer " + notExistentConsentToken)
                .when()
                .get("/account/v1/account/" + accountId)
                .then()
                .statusCode(403)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Forbidden"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + accountId))
                .body("_embedded.errors[0].message", containsString("is not in the right status"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }
}

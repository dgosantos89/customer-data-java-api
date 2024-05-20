package tests.accounts;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class GetAccountsTests extends TestBase {

    private static final String ACCOUNTS_ENDPOINT = "/account/v1/accounts";

    @Test
    // TC001 - Request accounts returns accounts list
    public void testValidAccountIdReturnsAccountDetails() {
        given()
                .header("Authorization", "Bearer " + getValidToken())
        .when()
                .get(ACCOUNTS_ENDPOINT)
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("data", hasSize(2))
                .body("data.id", containsInAnyOrder(
                        "87caf37b-f70f-440c-bacd-3b9399ca5d74",
                        "6565ab61-b27e-41e4-9ca2-f3ba83dbb669"))
                .body("data.bank", containsInAnyOrder("Nubank", "Itau"))
                .body("data.accountNumero", containsInAnyOrder("1234567-8", "8765432-1"))
                .body("links.self", equalTo("localhost:8080/test-api/account/v1/accounts"))
                .body("meta.totalRecords", equalTo(2))
                .body("meta.totalPages", equalTo(1))
                .body("meta.requestDateTime", notNullValue());
    }

    @Test
    // TC002 - No authentication token returns unauthorized error
    public void testNoAuthTokenReturnsUnauthorizedError() {
        when()
                .get(ACCOUNTS_ENDPOINT)
        .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Unauthorized"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNTS_ENDPOINT))
                .body("_embedded.errors[0].message", equalTo("Unauthorized"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    // TC003 - Request with invalid token returns unauthorized error
    public void testInvalidTokenReturnsUnauthorizedError() {
        String invalidToken = "INVALID_TOKEN";

        given()
                .header("Authorization", "Bearer " + invalidToken)
        .when()
                .get(ACCOUNTS_ENDPOINT)
        .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Unauthorized"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNTS_ENDPOINT))
                .body("_embedded.errors[0].message", equalTo("Unauthorized"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    // TC004 - Request with an invalid token payload returns internal server error
    public void testInvalidTokenPayloadReturnsInternalServerError() {
        String tokenWithoutPayload = generateTokenWithoutPayload();

        given()
                .header("Authorization", "Bearer " + tokenWithoutPayload)
        .when()
                .get(ACCOUNTS_ENDPOINT)
        .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Internal Server Error"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNTS_ENDPOINT))
                .body("_embedded.errors[0].message", equalTo("Internal Server Error: Cannot invoke \"java.util.Map.getOrDefault(Object, Object)\" because \"payload\" is null"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }

    @Test
    // TC005 - Request with a token scope CONSENTS_MANAGE returns unauthorized error
    public void testConsentsManageScopeReturnsUnauthorizedError() {
        String consentsToken = generateConsentToken();

        given()
                .header("Authorization", "Bearer " + consentsToken)
        .when()
                .get(ACCOUNTS_ENDPOINT)
        .then()
                .statusCode(403)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Forbidden"))
                .body("_links.self.href", equalTo("/test-api" + ACCOUNTS_ENDPOINT))
                .body("_embedded.errors[0].message", equalTo("Forbidden"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }
}
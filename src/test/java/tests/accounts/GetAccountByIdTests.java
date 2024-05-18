package tests.accounts;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.testBase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetAccountByIdTests extends testBase {

    @Test
    //TC001 - Valid account Id returns account data
    public void testValidAccountIdReturnsAccountDetails() {
        String accountId = "87caf37b-f70f-440c-bacd-3b9399ca5d74";

        given()
                .header("Authorization", "Bearer " + accessToken)
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
                .header("Authorization", "Bearer " + accessToken)
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
                .header("Authorization", "Bearer " + accessToken)
        .when()
                .get("/account/v1/account/" + shortAccountId)
        .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Not Found"))
                .body("_links.self.href", equalTo("/test-api/account/v1/account/" + shortAccountId))
                .body("_embedded.errors[0].message", equalTo("Account Id " + shortAccountId + " not found"))
                .body("_embedded.errors[0]._links", anEmptyMap())
                .body("_embedded.errors[0]._embedded", anEmptyMap());
    }
}

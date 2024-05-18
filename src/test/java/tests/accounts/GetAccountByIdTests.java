package tests.accounts;

import io.restassured.http.ContentType;
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
}

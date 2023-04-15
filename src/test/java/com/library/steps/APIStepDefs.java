package com.library.steps;

import com.library.pages.BasePage_EY;
import com.library.pages.BookPage;
import com.library.pages.BookPage_EY;
import com.library.pages.LoginPage;
import com.library.utility.BrowserUtil;
import com.library.utility.ConfigurationReader;
import com.library.utility.DB_Util;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class APIStepDefs extends BasePage_EY{

    RequestSpecification givenPart;
    Response response;
    ValidatableResponse thenPart;
    /**
     * US 01 RELATED STEPS
     *
     */
    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String userType) {

        givenPart = given().log().uri()
                .header("x-library-token", LibraryAPI_Util.getToken(userType));
    }
    @Given("Accept header is {string}")
    public void accept_header_is(String contentType) {
        givenPart.accept(contentType);
    }

    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endpoint) {
        response = givenPart.when().get(ConfigurationReader.getProperty("library.baseUri") + endpoint).prettyPeek();
        thenPart = response.then();
    }
    @Then("status code should be {int}")
    public void status_code_should_be(Integer statusCode) {
        thenPart.statusCode(statusCode);
    }
    @Then("Response Content type is {string}")
    public void response_content_type_is(String contentType) {
        thenPart.contentType(contentType);
    }
    @Then("{string} field should not be null")
    public void field_should_not_be_null(String path) {
        thenPart.body(path, is(notNullValue()));
    }


    // -----US03-----

    @And("Request Content Type header is {string}")
    public void requestContentTypeHeaderIs(String contentType) {
        givenPart.contentType(contentType);
    }

    Map<String, Object> randomBook;
    @And("I create a random {string} as request body")
    public void i_create_a_random_as_request_body(String random) {
        if (random.equals("book")){
            this.randomBook = LibraryAPI_Util.getRandomBookMap();
        }else{
            System.exit(1);   // yes?
        }
    }

    @When("I send POST request to {string} endpoint")
    public void i_send_post_request_to_endpoint(String post) {
        response = givenPart.formParams(randomBook).post(ConfigurationReader.getProperty("library.baseUri") +
                post);
        thenPart = response.then();
    }

    @Then("the field value for {string} path should be equal to {string}")
    public void the_field_value_for_path_should_be_equal_to(String path, String message) {
        Assert.assertEquals(response.path(path), message);
    }

    LoginPage loginPage = new LoginPage();
    @And("I logged in Library UI as {string}")
    public void iLoggedInLibraryUIAs(String user) {
        loginPage.login(user);
    }

    @And("I navigate to {string} page")
    public void iNavigateToPage(String page) {
        books.click();

        String expectedHeader = "Book Management";
        String actualHeader = pageHeader.getText();

        System.out.println("expectedHeader = " + expectedHeader);
        System.out.println("actualHeader = " + actualHeader);

        Assert.assertEquals(actualHeader, expectedHeader);
    }

    BookPage_EY bookPage = new BookPage_EY();
    @And("UI, Database and API created book information must match")
    public void uiDatabaseAndAPICreatedBookInformationMustMatch() {
        // get data from API --> get book_id, book name
        response.prettyPrint();
        JsonPath jsonPath = response.jsonPath();

        String book_id = jsonPath.getString("book_id");
        System.out.println("book_id = " + book_id);

        Response response1 = givenPart
                .pathParam("id", book_id)
                .when().get(ConfigurationReader.getProperty("library.baseUri") + "/get_book_by_id/{id}");

        JsonPath jsonPath1 = response1.jsonPath();
        String APIName = jsonPath1.getString("name");
        System.out.println("APIName = " + APIName);

        // get data from DB --> get book information based on book_id retrieved from API
        String query = "select * from books where id ='" + book_id + "'";

        // run query
        DB_Util.runQuery(query);

        // get DB results
        Map<String, Object> dbMap = DB_Util.getRowMap(1);
        System.out.println("dbMap = " + dbMap);

        Object dbID = dbMap.get("id");
        System.out.println("dbID = " + dbID);
        Object dbName = dbMap.get("name");
        System.out.println("dbName = " + dbName);

        // API vs. DB --> compare book_id from DB to API
         Assert.assertEquals(dbID, book_id);

         // get data from UI
        bookPage.search.sendKeys(dbName.toString());
        BrowserUtil.waitFor(3);

        // UI vs. DB --> compare book name from UI to DB
        String UIName = bookPage.tableFirstRowNameColumn.getText();
        Assert.assertEquals(UIName, dbName);

        // UI vs. API --> compare book name from UI to API
        Assert.assertEquals(UIName, APIName);

    }

}

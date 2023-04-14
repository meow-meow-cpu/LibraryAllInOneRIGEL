package com.library.steps;

import com.library.utility.ConfigurationReader;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class APIStepDefs {

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

    @And("I logged in Library UI as {string}")
    public void iLoggedInLibraryUIAs(String user) {

    }


    @And("I navigate to {string} page")
    public void iNavigateToPage(String page) {

    }


    @And("UI, Database and API created book information must match")
    public void uiDatabaseAndAPICreatedBookInformationMustMatch() {

    }
    /*

    @And("created user information should match with Database")
    public void createdUserInformationShouldMatchWithDatabase() {
        DB_Util.runQuery("select * from users where id = "+response.path("user_id") +";");
       // System.out.println("DB_Util.getFirstRowFirstColumn() = " + DB_Util.getFirstRowFirstColumn());
        Assert.assertFalse(DB_Util.getFirstRowFirstColumn().isEmpty());
    }

    BasePage basePage = new BasePage() {
        @Override
        public void navigateModule(String moduleName) {
            super.navigateModule(moduleName);
        }
    };
    LoginPage loginPage = new LoginPage();
    UsersPageAO usersPageAO = new UsersPageAO();
    @And("created user should be able to login Library UI")
    public void createdUserShouldBeAbleToLoginLibraryUI() {
//        DB_Util.runQuery("select * from users where id = "+response.path("user_id") +";");
//        String email = DB_Util.getCellValue(1,"email");
//        String password = DB_Util.getCellValue(1,"password");
//
//        loginPage.login(email, password);
        loginPage.login(ConfigurationReader.getProperty("librarian_username"),ConfigurationReader.getProperty("librarian_password") );
    }

    @And("created user name should appear in Dashboard Page")
    public void createdUserNameShouldAppearInDashboardPage() {
        BrowserUtil.waitFor(2);
        basePage.navigateModule("Users");
        BrowserUtil.waitFor(5);
        usersPageAO.searchUser.click();
        usersPageAO.searchUser.sendKeys("Connie");
        BrowserUtil.waitFor(3);
        //usersPageAO.verifyUserExists(response.path("name"));
        usersPageAO.verifyUserExists("Connie");
    }
     */
}

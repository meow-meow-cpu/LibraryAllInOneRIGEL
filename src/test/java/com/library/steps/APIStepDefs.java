package com.library.steps;

import com.library.pages.BasePage;
import com.library.pages.LoginPage;
import com.library.pages.UsersPageAO;
import com.library.utility.BrowserUtil;
import com.library.utility.ConfigurationReader;
import com.library.utility.DB_Util;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class APIStepDefs {

    RequestSpecification givenPart;
    Response response;
    ValidatableResponse thenPart;
    String pathParamKey;
    String pathParamValue;
    static Map<String, Object> requestBody = new LinkedHashMap<>();

    static String newEmailAPI;
    static String newPasswordAPI;
    static String newUserNameAPI;



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
        response.prettyPeek();
    }
    @Then("status code should be {int}")
    public void status_code_should_be(Integer statusCode) {
        thenPart.statusCode(statusCode);
        System.out.println("thenPart.statusCode(statusCode) = " + thenPart.statusCode(statusCode));
    }
    @Then("Response Content type is {string}")
    public void response_content_type_is(String contentType) {
        thenPart.contentType(contentType);
    }
    @Then("{string} field should not be null")
    public void field_should_not_be_null(String path) {
        //thenPart.body(path, everyItem(notNullValue()));
        Assert.assertNotNull(response.path(path));
    }

    //--------------US02------------------Start----------------------
    @And("Path param is {string}")
    public void pathParamIs(String pathParamValue) {
       this.pathParamValue=pathParamValue;
       // givenPart.pathParam(pathParamValue, pathParamKey);
    }

    @When("I send GET request to {string} endpoint AO")
    public void i_send_get_request_to_endpoint_AO(String endpoint) {
        response = givenPart.when().pathParam("id" ,pathParamValue).get(ConfigurationReader.getProperty("library.baseUri") + endpoint).prettyPeek();
        thenPart = response.then();
        //need to unhardcode id two lines above!!!!!!!!
    }

    @And("{string} field should be same with path param")
    public void fieldShouldBeSameWithPathParam(String pathParamKey) {
       Assert.assertEquals(response.path(pathParamKey),pathParamValue);
    }

    @And("following fields should not be null")
    public void followingFieldsShouldNotBeNull(List<String> expectedFields) {
        for (String eachField : expectedFields) {
            thenPart.body(eachField, notNullValue());
            System.out.println("response.jsonPath().get(eachField) = " + response.jsonPath().get(eachField));
        }
    }

    //--------------US02------------------FINISH---------------------
    //--------------US04------------------START)---------------------

    @And("Request Content Type header is {string}")
    public void requestContentTypeHeaderIs(String contentType) {
        givenPart.contentType(contentType);
    }
    Map<String, Object> randomUser;
    @Given("I create a random {string} as request body")
    public void i_create_a_random_as_request_body(String bookOrUser) {

        switch (bookOrUser) {
            case "book":
                requestBody = LibraryAPI_Util.getRandomBookMap();
                break;

            case "user":
                requestBody = LibraryAPI_Util.getRandomUserMap();
                newUserNameAPI = (String) requestBody.get("full_name");
                newEmailAPI = (String) requestBody.get("email");
                newPasswordAPI = (String) requestBody.get("password");
                break;
        }

        givenPart = givenPart.formParams(requestBody);
    }
    @When("I send POST request to {string} endpoint")
    public void iSendPOSTRequestToEndpoint(String postRequest) {

            response = givenPart.post(ConfigurationReader.getProperty("library.baseUri") + postRequest).prettyPeek();

        thenPart = response.then();
    }

    @And("the field value for {string} path should be equal to {string}")
    public void theFieldValueForPathShouldBeEqualTo(String fieldName, String fieldValue) {
        Assert.assertEquals(response.path(fieldName), fieldValue);
    }

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
//    @And("created user should be able to login Library UI")
//    public void createdUserShouldBeAbleToLoginLibraryUI() {
////        DB_Util.runQuery("select * from users where id = "+response.path("user_id") +";");
////        String email = DB_Util.getCellValue(1,"email");
////        String password = DB_Util.getCellValue(1,"password");
////
////        loginPage.login(email, password);
//        loginPage.login(ConfigurationReader.getProperty("librarian_username"),ConfigurationReader.getProperty("librarian_password") );
//    }
//
//    @And("created user name should appear in Dashboard Page")
//    public void createdUserNameShouldAppearInDashboardPage() {
//        BrowserUtil.waitFor(2);
//        basePage.navigateModule("Users");
//        BrowserUtil.waitFor(5);
//        usersPageAO.searchUser.click();
//        usersPageAO.searchUser.sendKeys("Connie");
//        BrowserUtil.waitFor(3);
//        //usersPageAO.verifyUserExists(response.path("name"));
//        usersPageAO.verifyUserExists("Connie");
//    }

    @Then("created user should be able to login Library UI")
    public void created_user_should_be_able_to_login_library_ui() {
        System.out.println("newEmailAPI = " + newEmailAPI);
        System.out.println("newPasswordAPI = " + newPasswordAPI);
        loginPage.login(newEmailAPI, newPasswordAPI);
        BrowserUtil.waitFor(2);

    }

    @Then("created user name should appear in Dashboard Page")
    public void created_user_name_should_appear_in_dashboard_page() {

        String actualUserNameUI = loginPage.accountHolderName.getText();

        System.out.println("actualUserNameUI = " + actualUserNameUI);
        System.out.println("newUserNameAPI = " + newUserNameAPI);

        Assert.assertEquals(newUserNameAPI, actualUserNameUI);


    }

    //----------------US04 Finished------------------------

    String email;
    String password;
    @Given("I logged Library api with credentials {string} and {string}")
    public void iLoggedLibraryApiWithCredentialsAnd(String email, String password) {
        givenPart = given().log().uri();
        this.email = email;
        this.password = password;
    }

    @And("I send token information as request body")
    public void iSendTokenInformationAsRequestBody() {
        String token = LibraryAPI_Util.getToken(email, password);
        System.out.println("token = " + token);
        givenPart.formParam("token",token).when();

    }
}

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

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class APIStepDefs {

    RequestSpecification givenPart;
    Response response;
    ValidatableResponse thenPart;
    String pathParamKey;
    String pathParamValue;

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
        thenPart.body(path, everyItem(notNullValue()));
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

    @And("I create a random {string} as request body")
    public void iCreateARandomAsRequestBody(String arg0) {
    }

    @When("I send POST request to {string} endpoint")
    public void iSendPOSTRequestToEndpoint(String arg0) {
    }

    @And("the field value for {string} path should be equal to {string}")
    public void theFieldValueForPathShouldBeEqualTo(String arg0, String arg1) {
    }

    @And("created user information should match with Database")
    public void createdUserInformationShouldMatchWithDatabase() {
    }

    @And("created user should be able to login Library UI")
    public void createdUserShouldBeAbleToLoginLibraryUI() {
    }

    @And("created user name should appear in Dashboard Page")
    public void createdUserNameShouldAppearInDashboardPage() {
    }




}

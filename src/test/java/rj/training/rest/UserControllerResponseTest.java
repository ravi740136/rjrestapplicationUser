package rj.training.rest;

	import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

	public class UserControllerResponseTest {

	    private RequestSpecification requestSpec;

	    @BeforeClass
	    public void setup() {
	        RestAssured.baseURI = "http://localhost:8080";
	        requestSpec = new RequestSpecBuilder()
	                .setBasePath("/api/users")
	                .setContentType(ContentType.JSON)
	                .build();
	    }

	    // Test Case 1: Status Code
	    @Test
	    public void testStatusCodeForGetUser() {
	        given().spec(requestSpec)
	        .when().get("/1")
	        .then().statusCode(200);
	    }

	    // Test Case 2: Response Headers
	    @Test
	    public void testResponseHeaders() {
	        given().spec(requestSpec)
	        .when().get("/1")
	        .then()
	        .header("Content-Type", "application/json")
	        .header("Content-Length", notNullValue());
	    }

	    // Test Case 3: Response Cookies
	    @Test
	    public void testResponseCookies() {
	        given().spec(requestSpec)
	        .when().get("/1")
	        .then()
	        .cookie("session_id", notNullValue())
	        .cookie("logged_in", equalTo("true"));
	    }

	    // Test Case 4: Response Body Fields
	    @Test
	    public void testResponseBodyFields() {
	        given().spec(requestSpec)
	        .when().get("/1")
	        .then()
	        .body("id", equalTo(1))
	        .body("name", equalTo("John Doe"))
	        .body("email", equalTo("john@example.com"));
	    }

	    // Test Case 5: Response Body Field Types
	    @Test
	    public void testResponseBodyFieldTypes() {
	        given().spec(requestSpec)
	        .when().get("/1")
	        .then()
	        .body("id", instanceOf(Integer.class))
	        .body("name", instanceOf(String.class))
	        .body("email", instanceOf(String.class));
	    }

	    // Test Case 6: Null Values in Fields
	    @Test
	    public void testNullValuesInFields() {
	        given().spec(requestSpec)
	        .when().get("/1")
	        .then()
	        .body("token", nullValue());
	    }

	    // Test Case 7: List Size in Response Body
	    @Test
	    public void testResponseListSize() {
	        given().spec(requestSpec)
	        .when().get()
	        .then()
	        .body("size()", greaterThan(0));
	    }

	    // Test Case 8: Nested JSON Fields
	    @Test
	    public void testNestedFields() {
	        given().spec(requestSpec)
	        .when().get("/1")
	        .then()
	        .body("address.street", equalTo("Main St"))
	        .body("address.city", equalTo("Metropolis"));
	    }

	    // Test Case 9: Conditional Response Fields
	    @Test
	    public void testConditionalFields() {
	        given().spec(requestSpec)
	        .when().get("/premiumUser")
	        .then()
	        .body("premiumFeatures", hasSize(greaterThan(0)))
	        .body("subscription", equalTo("premium"));
	    }

	    // Test Case 10: Specific Values in Arrays
	    @Test
	    public void testArrayValues() {
	        given().spec(requestSpec)
	        .when().get()
	        .then()
	        .body("name", hasItems("John Doe", "Alice", "Jane"));
	    }

	    // Test Case 11: Response Content Length
	    @Test
	    public void testResponseContentLength() {
	        given().spec(requestSpec)
	        .when().get("/1")
	        .then()
	        .header("Content-Length", Integer::parseInt, greaterThan(100));
	    }

	    // Test Case 12: Response Time
	    @Test
	    public void testResponseTime() {
	        given().spec(requestSpec)
	        .when().get("/1")
	        .then()
	        .time(lessThan(2000L));
	    }

	    // Additional Test: Creating User with JSON body
	    @Test
	    public void testCreateUser() {
	        String userJson = "{\"name\":\"John Doe\", \"email\":\"john@example.com\"}";

	        given()
	            .spec(requestSpec)
	            .body(userJson)
	        .when()
	            .post()
	        .then()
	            .statusCode(201)
	            .body("name", equalTo("John Doe"))
	            .body("email", equalTo("john@example.com"));
	    }

	    @Test
	    public void testUploadFile() {
	        File file = new File("src/main/resources/test.txt");

	        given()
	            .spec(requestSpec)
	            .contentType("multipart/form-data")  // Ensure the correct Content-Type
	            .multiPart("file", file)
	        .when()
	            .post("/upload")
	        .then()
	            .statusCode(200)
	            .body("fileName", equalTo("test.txt"))
	            .body("message", equalTo("File uploaded successfully"));
	    }

	    
	    // Test Case for Response Specification
	    @Test
	    public void testResponseSpec() {
	        given()
	            .spec(requestSpec)
	        .when()
	            .get("/1")
	        .then()
	            .spec(responseSpecification());
	    }

	    private ResponseSpecification responseSpecification() {
	        return RestAssured.expect()
	            .statusCode(200)
	            .contentType(ContentType.JSON)
	            .body("id", notNullValue())
	            .body("name", notNullValue())
	            .body("email", notNullValue());
	    }

	    // Test Case for Status Line
	    @Test
	    public void testStatusLine() {
	        given()
	            .spec(requestSpec)
	        .when()
	            .get("/1")
	        .then()
	            .statusLine("HTTP/1.1 200 ");
	    }
	}



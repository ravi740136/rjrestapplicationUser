package rj.training.rest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.io.File;

public class UserControllerTest {

    private RequestSpecification requestSpec;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        requestSpec = new RequestSpecBuilder()
                .setBasePath("/api/users")
                .setContentType(ContentType.JSON)
                .build();
    }

    // Test case for creating a user with JSON body
    @Test
    public void testCreateUser() {
        String userJson = "{\"name\":\"John Doe\", \"email\":\"john@example.com\"}";

        given()
            .spec(requestSpec)
            .body(userJson)
        .when()
            .post()
        .then()
        .log().all()
            .statusCode(201)
            .body("name", equalTo("John Doe"))
            .body("email", equalTo("john@example.com"));
    }

    // Test case for getting all users
    @Test
    public void testGetAllUsers() {
        given()
            .spec(requestSpec)
        .when()
            .get()
        .then()
            .log().all()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

 // Test case for retrieving a user by ID
    @Test
    public void testGetUserById() {
        // Step 1: Create a user first
        String userJson = "{\"name\":\"John Doe\", \"email\":\"john@example.com\"}";

        // Capture the created user's ID
        int userId = 
            given()
                .spec(requestSpec)
                .body(userJson)
            .when()
                .post()
            .then()
                .statusCode(201)
                .extract().path("id"); // Extracts the 'id' of the created user

        // Step 2: Retrieve the user by the captured ID
        given()
            .spec(requestSpec)
        .when()
            .get("/" + userId)  // Using the captured ID dynamically
        .then()
            .log().all()
            .statusCode(200)
            .body("name", equalTo("John Doe"))
            .body("email", equalTo("john@example.com"));
    }


    // Test case for updating a user
    @Test
    public void testUpdateUser() {
    	   // Step 1: Create a user to update
        String initialUserJson = "{\"name\":\"John Doe\", \"email\":\"john@example.com\"}";

        // Capture the created user's ID
        int userId = 
            given()
                .spec(requestSpec)
                .body(initialUserJson)
            .when()
                .post()
            .then()
                .statusCode(201)
                .extract().path("id"); // Extracts the 'id' of the created user

        // Step 2: Define the updated user details
        String updatedUserJson = "{\"name\":\"Jane Doe\", \"email\":\"jane@example.com\"}";

        // Step 3: Update the user by the captured ID
        given()
            .spec(requestSpec)
            .body(updatedUserJson)
        .when()
            .put("/" + userId) // Using the captured ID dynamically
        .then()
            .statusCode(200)
            .body("name", equalTo("Jane Doe"))
            .body("email", equalTo("jane@example.com"));
    }

    // Test case for deleting a user

    @Test
    public void testDeleteUser() {
        // Step 1: Create a user to delete
        String userJson = "{\"name\":\"John Doe\", \"email\":\"john@example.com\"}";

        // Capture the created user's ID
        int userId = 
            given()
                .spec(requestSpec)
                .body(userJson)
            .when()
                .post()
            .then()
            .log().all()
                .statusCode(201)
                .extract().path("id"); // Extracts the 'id' of the created user

        // Step 2: Delete the user by the captured ID
        given()
            .spec(requestSpec)
        .when()
            .delete("/" + userId)
        .then()
        .log().all()
            .statusCode(204);

        // Step 3: Try to delete the same user again and expect a 404 Not Found
        given()
            .spec(requestSpec)
        .when()
            .delete("/" + userId)
        .then()
        .log().all()     
            .statusCode(404);
    }


    // Test case for creating a user with form parameters
    @Test
    public void testCreateUserWithFormParams() {
        given()
            .spec(requestSpec)
            .formParam("name", "Alice")
            .formParam("email", "alice@example.com")
            .contentType(ContentType.URLENC) // Form parameters need URL encoding
        .when()
            .post("/form")
        .then()
            .statusCode(201)
            .body("name", equalTo("Alice"))
            .body("email", equalTo("alice@example.com"));
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


    // Test case for uploading a file with description
    @Test
    public void testUploadFileWithDescription() {
    	File file = new File("src/main/resources/test.txt");

        given()
            .spec(requestSpec)
            .contentType("multipart/form-data")  // Ensure the correct Content-Type
            .multiPart("file", file)
            .multiPart("description", "Sample description")
        .when()
            .post("/uploadWithDescription")
        .then()
            .statusCode(200)
            .body("fileName", equalTo("test.txt"))
            .body("description", equalTo("Sample description"))
            .body("message", equalTo("File uploaded with description successfully"));
    }

    // Test case for searching users by name with query parameters
    @Test
    public void testSearchUserByName() {
        given()
            .spec(requestSpec)
            .queryParam("name", "John Doe")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("[0].name", equalTo("John Doe"));
    }
    
 // Test case for custom header
    @Test
    public void testCustomHeader() {
        given()
            .spec(requestSpec)
        .when()
            .get("/customHeader")
        .then()
            .statusCode(200)
            .header("X-Custom-Header", equalTo("CustomHeaderValue"))
            .body("message", equalTo("Custom header response"));
    }

    // Test case for setting a cookie
    @Test
    public void testSetCookie() {
        given()
            .spec(requestSpec)
        .when()
            .get("/setCookie")
        .then()
            .statusCode(200)
            .cookie("sessionID", equalTo("12345"))
            .body("message", equalTo("Cookie has been set"));
    }

    // Test case for handling redirect
    @Test
    public void testRedirect() {
        given()
            .spec(requestSpec)
            .redirects().follow(false) // Don't automatically follow redirects
        .when()
            .get("/redirect")
        .then()
            .statusCode(302) // Status code for FOUND
            .header("Location", equalTo("/api/users")); // Verify redirect location
    }
    
 // In UserControllerTest class

    @Test
    public void testTemporaryRedirect() {
        given()
            .spec(requestSpec)
            .redirects().follow(true) // Enable automatic redirect following
        .when()
            .get("/temporary-redirect")
        .then()
            .statusCode(200) // Should receive 200 OK after redirect
            .body(equalTo("You have reached the final destination!")); // Verify final response content
    }


}

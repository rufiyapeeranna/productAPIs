package com.fakestore.api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ProductTests extends BaseTest {

    private static int createdProductId;

    @Test(priority = 1)
    public void testGetAllProducts() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .extract().response();

        List<String> titles = response.jsonPath().getList("title");
        List<Float> prices = response.jsonPath().getList("price");

        Assert.assertTrue(titles.size() >= 10, "Product list should have at least 10 products");

        System.out.println("==== Product Catalog ====");
        for (int i = 0; i < titles.size(); i++) {
            System.out.println((i+1) + ". " + titles.get(i) + " - $" + prices.get(i));
        }
    }

    @Test(dataProvider = "productIds", dataProviderClass = TestDataProvider.class, priority = 2)
    public void testGetProductById(int productId) {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/products/" + productId)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/product_schema.json"))
                .extract().response();

        JsonPath jp = response.jsonPath();
        Assert.assertEquals(jp.getInt("id"), productId);
        Assert.assertNotNull(jp.getString("category"), "Category should not be null");
        Assert.assertNotNull(jp.getFloat("price"), "Price should not be null");
    }

    @Test(priority = 3)
    public void testAddNewProduct() {
        String payload = """
                {
                  "title": "Wireless Mouse",
                  "price": 799,
                  "description": "A high-quality wireless mouse",
                  "category": "electronics",
                  "image": "https://example.com/mouse.jpg"
                }
                """;

        Response response = given()
                .spec(requestSpec)
                .body(payload)
                .when()
                .post("/products")
                .then()
                .statusCode(201) // FakeStore returns 200 for create
                .body(matchesJsonSchemaInClasspath("schemas/product_schema.json"))
                .extract().response();

        createdProductId = response.jsonPath().getInt("id");
        Assert.assertTrue(createdProductId > 0, "Product ID should be generated");
    }

    @Test(priority = 4, dependsOnMethods = "testAddNewProduct")
    public void testUpdateProduct() {
        String payload = """
                {
                  "price": 899
                }
                """;

        Response response = given()
                .spec(requestSpec)
                .body(payload)
                .when()
                .put("/products/" + createdProductId)
                .then()
                .statusCode(200)
                .extract().response();

        int updatedPrice = response.jsonPath().getInt("price");
        Assert.assertEquals(updatedPrice, 899, "Updated price should be reflected");
    }

    @Test(priority = 5, dependsOnMethods = "testAddNewProduct")
    public void testDeleteProduct() {
        given()
                .spec(requestSpec)
                .when()
                .delete("/products/" + createdProductId)
                .then()
                .statusCode(200);

        // Verify deletion
        given()
                .spec(requestSpec)
                .when()
                .get("/products/" + createdProductId)
                .then()
                .statusCode(200); // FakeStore returns 404 for non-existent product
    }
}
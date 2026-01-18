package com.fakestore.api;


import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    protected static RequestSpecification requestSpec;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://fakestoreapi.com";

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(RestAssured.baseURI)
                .setContentType("application/json")
                .build();
    }
}


package com.fakestore.api;

import org.testng.annotations.DataProvider;

public class TestDataProvider {

    @DataProvider(name = "productIds")
    public Object[][] productIds() {
        return new Object[][] {
                {1},
                {2},
                {3}
        };
    }
}


package com.example.ec_geocustomer.recommendation;

public class RecommendationData {
    String Imageurl,productName;

    public RecommendationData(String imageurl, String productName) {
        Imageurl = imageurl;
        this.productName = productName;
    }

    public RecommendationData() {
    }

    public String getImageurl() {
        return Imageurl;
    }

    public String getProductName() {
        return productName;
    }
}

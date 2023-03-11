package com.example.ec_geocustomer.recommendation;

public class RecommendationData {
    String imageurl,productName;


    public RecommendationData(String imageurl, String productName) {
        this.imageurl = imageurl;
        this.productName = productName;
    }

    public RecommendationData() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getProductName() {
        return productName;
    }
}

package com.example.ec_geocustomer.data;

public class Shop extends ShopProfile{

    Long quantity;
    Double discount;
    String email;

    public Shop(String shopname, String ownername, String address, String city, Long mobile, Double latitude, Double longitude, Long quantity, Double discount, String email) {
        super(shopname, ownername, address, city, mobile, latitude, longitude);
        this.quantity = quantity;
        this.discount = discount;
        this.email = email;
    }

    public Shop() {
    }
}

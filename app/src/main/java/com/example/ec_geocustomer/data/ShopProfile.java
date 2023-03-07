package com.example.ec_geocustomer.data;

public class ShopProfile {
    String shopname, ownername, address, city;
    Long mobile;

    Double latitude,longitude;

    public ShopProfile(String shopname, String ownername, String address, String city, Long mobile, Double latitude, Double longitude) {
        this.shopname = shopname;
        this.ownername = ownername;
        this.address = address;
        this.city = city;
        this.mobile = mobile;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ShopProfile() {
    }

    public String getShopname() {
        return shopname;
    }

    public String getOwnername() {
        return ownername;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public Long getMobile() {
        return mobile;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}

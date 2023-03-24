package com.example.ec_geocustomer.data;

import java.io.Serializable;

public class Shop implements Serializable {

    String shopname, ownername, address, city,upiId;
    Long mobile;

    Double latitude,longitude;
    Long quantity;
    Double discount;
    String email;

    public Shop(String shopname, String ownername, String address, String city, String upiId, Long mobile, Double latitude, Double longitude, Long quantity, Double discount, String email) {
        this.shopname = shopname;
        this.ownername = ownername;
        this.address = address;
        this.city = city;
        this.upiId = upiId;
        this.mobile = mobile;
        this.latitude = latitude;
        this.longitude = longitude;
        this.quantity = quantity;
        this.discount = discount;
        this.email = email;
    }

    public Shop() {
    }

    public Long getQuantity() {
        return quantity;
    }

    public Double getDiscount() {
        return discount;
    }

    public String getEmail() {
        return email;
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

    public String getUpiId() {
        return upiId;
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

package com.example.ec_geocustomer.data;

import java.io.Serializable;

public class Profile implements Serializable {

    String name,address,city;
    Long mobile;
    Double latitude,longitude;

    public Profile(String name, String address, String city, Long mobile, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.mobile = mobile;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Profile() {
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }
}

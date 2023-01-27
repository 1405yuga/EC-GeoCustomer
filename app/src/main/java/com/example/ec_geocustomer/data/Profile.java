package com.example.ec_geocustomer.data;

import java.io.Serializable;

public class Profile implements Serializable {

    String name,address,city;
    Long mobile;

    public Profile(String name, String address, String city, Long mobile) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.mobile = mobile;
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
}

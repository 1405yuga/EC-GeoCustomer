package com.example.ec_geocustomer.data;

public class Availability {

    Long quantity;
    Double discount;

    public Availability() {
    }

    public Availability(Long quantity, Double discount) {

        this.quantity = quantity;
        this.discount = discount;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Double getDiscount() {
        return discount;
    }
}

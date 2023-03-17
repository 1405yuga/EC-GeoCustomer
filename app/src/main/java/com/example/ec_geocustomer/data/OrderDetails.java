package com.example.ec_geocustomer.data;

public class OrderDetails {

    String transactionID,time,customerEmail,ownerEmail,barcode,orderStatus;
    Double total;
    Long qty;

    public OrderDetails(String transactionID, String time, String customerEmail, String ownerEmail, String barcode, String orderStatus, Double total, Long qty) {
        this.transactionID = transactionID;
        this.time = time;
        this.customerEmail = customerEmail;
        this.ownerEmail = ownerEmail;
        this.barcode = barcode;
        this.orderStatus = orderStatus;
        this.total = total;
        this.qty = qty;
    }

    public OrderDetails() {
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getTime() {
        return time;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public Double getTotal() {
        return total;
    }

    public Long getQty() {
        return qty;
    }
}

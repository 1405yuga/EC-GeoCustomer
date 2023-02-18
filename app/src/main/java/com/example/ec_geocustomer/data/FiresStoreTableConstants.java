package com.example.ec_geocustomer.data;

public class FiresStoreTableConstants {
    final String barcode = "Barcode";
    final String customer = "Customer";
    final String owner = "Owner";

    //to fetch from barcode database
    final String barcodeName = "Name";
    final String barcodeCategory = "Category";
    final String barcodeSubCatgeory = "Sub-category";
    final String barcodeBrand = "Brand";
    final String barcodePrice = "Price";
    final String barcodeSize = "size";
    final String barcodeUrl = "url";


    //owner database
    final String ownerAvailability = "Availability";
    final String ownerDiscount="discount";
    final String ownerQuantity="quantity";

    //customer database
    final String customerProfile="Profile";

    public String getBarcodeUrl() {
        return barcodeUrl;
    }

    public String getOwnerDiscount() {
        return ownerDiscount;
    }

    public String getOwnerQuantity() {
        return ownerQuantity;
    }

    public String getOwnerAvailability() {
        return ownerAvailability;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getCustomer() {
        return customer;
    }

    public String getOwner() {
        return owner;
    }

    public String getBarcodeName() {
        return barcodeName;
    }

    public String getBarcodeCategory() {
        return barcodeCategory;
    }

    public String getBarcodeSubCatgeory() {
        return barcodeSubCatgeory;
    }

    public String getBarcodeBrand() {
        return barcodeBrand;
    }

    public String getBarcodePrice() {
        return barcodePrice;
    }

    public String getBarcodeSize() {
        return barcodeSize;
    }

    public String getCustomerProfile() {
        return customerProfile;
    }
}

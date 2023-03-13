package com.example.ec_geocustomer.data;

import java.io.Serializable;

public class ItemBarcode implements Serializable {
    String barcode,name,category,subCategory,size,url,brand;
    Double mrp;

    public ItemBarcode(String barcode, String name, String category, String subCategory, String size, String url, String brand, Double mrp) {
        this.barcode = barcode;
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
        this.size = size;
        this.url = url;
        this.brand = brand;
        this.mrp = mrp;
    }

    public ItemBarcode() {
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public String getBrand() {
        return brand;
    }

    public Double getMrp() {
        return mrp;
    }
}

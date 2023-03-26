package com.example.ec_geocustomer.data;

public class MarkerTagShopItembarcode {
    ItemBarcode itemBarcode;
    Shop shop;

    public MarkerTagShopItembarcode(ItemBarcode itemBarcode, Shop shop) {
        this.itemBarcode = itemBarcode;
        this.shop = shop;
    }

    public MarkerTagShopItembarcode() {
    }

    public ItemBarcode getItemBarcode() {
        return itemBarcode;
    }

    public Shop getShop() {
        return shop;
    }
}

package com.example.ec_geocustomer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.ec_geocustomer.data.ItemBarcode;
import com.example.ec_geocustomer.data.Shop;
import com.example.ec_geocustomer.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {

    ActivityPaymentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Shop shop= (Shop) getIntent().getSerializableExtra("shop");
        ItemBarcode itemBarcode= (ItemBarcode) getIntent().getSerializableExtra("itembarcode");
        Double d=itemBarcode.getMrp();
        final Double newPrice=d*(100-shop.getDiscount())/100;
        final Double total=newPrice*Double.parseDouble(getIntent().getStringExtra("qty_purchased"));
        binding.discount.setText(shop.getDiscount().toString());
        binding.qtyAvail.setText(shop.getQuantity().toString());
        binding.qtyPurchased.setText(getIntent().getStringExtra("qty_purchased"));
        Log.d(TAG,"qty purchased received "+getIntent().getStringExtra("qty_purchased"));
        Glide
                .with(this)
                .load(itemBarcode.getUrl())
                .centerCrop()
                .into(binding.productImage);
        binding.productName.setText(itemBarcode.getName());
        binding.oldPrice.setText(itemBarcode.getMrp().toString());
        binding.price.setText(newPrice.toString());
        binding.newPrice.setText(newPrice.toString());
        binding.total.setText(total.toString());
        binding.shopName1.setText(shop.getShopname());
        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 12-03-2023 integrate payment gateway

            }
        });



    }
}
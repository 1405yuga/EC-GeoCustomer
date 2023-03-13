package com.example.ec_geocustomer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ec_geocustomer.data.FiresStoreTableConstants;
import com.example.ec_geocustomer.data.ItemBarcode;
import com.example.ec_geocustomer.data.Shop;
import com.example.ec_geocustomer.databinding.ActivityPaymentBinding;
import com.example.ec_geocustomer.databinding.RecommendDialogBinding;
import com.example.ec_geocustomer.recommendation.RecommendationAdapter;
import com.example.ec_geocustomer.recommendation.RecommendationData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.shreyaspatil.easyupipayment.EasyUpiPayment;
import dev.shreyaspatil.easyupipayment.exception.AppNotFoundException;
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener;
import dev.shreyaspatil.easyupipayment.model.PaymentApp;
import dev.shreyaspatil.easyupipayment.model.TransactionDetails;

public class PaymentActivity extends AppCompatActivity {

    ActivityPaymentBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    FiresStoreTableConstants constants=new FiresStoreTableConstants();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
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
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
                String transcId = df.format(c);
                try {
                    EasyUpiPayment.Builder builder = new EasyUpiPayment.Builder(PaymentActivity.this)
                            .setPayeeVpa("example@vpa")
                            .setPayeeName("yuga")
                            .setPayeeMerchantCode("1234")
                            .setTransactionId(transcId)
                            .setTransactionRefId(transcId)
                            .setDescription(itemBarcode.getName())
                            .setAmount(total.toString());

                    EasyUpiPayment easyUpiPayment = builder.build();
                    easyUpiPayment.startPayment();
                    easyUpiPayment.setPaymentStatusListener(new PaymentStatusListener() {
                        @Override
                        public void onTransactionCompleted(@NonNull TransactionDetails transactionDetails) {
                            Toast.makeText(PaymentActivity.this, "Ordered placed successfully !", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"transaction completed");
                        }

                        @Override
                        public void onTransactionCancelled() {
                            Toast.makeText(PaymentActivity.this, "Transaction Cancelled!", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"transaction cancelled");
                        }
                    });
                } catch (AppNotFoundException e) {
                    Toast.makeText(PaymentActivity.this, "You don't have UPI application for payment !", Toast.LENGTH_SHORT).show();
                    e.getStackTrace();
                }




            }
        });


    }

}
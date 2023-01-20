package com.example.ec_geocustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ec_geocustomer.databinding.ActivitySignUpBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 25-10-2022 check if all boxes are filled and verify mobile
                
                //checking if mobile empty
                if(binding.mobile.getEditText().getText().toString().isEmpty()){
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(SignUp.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                }
                else if(binding.mobile.getEditText().getText().toString().length()!=10){
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(SignUp.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.submitBtn.setVisibility(View.INVISIBLE);
                    //get the otp
                    FirebaseAuth mAuth=FirebaseAuth.getInstance();
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber("+91"+binding.mobile.getEditText().getText().toString())       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(SignUp.this)                 // Activity (for callback binding)
                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            binding.progressBar.setVisibility(View.GONE);
                                            binding.submitBtn.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            binding.progressBar.setVisibility(View.INVISIBLE);
                                            binding.submitBtn.setVisibility(View.VISIBLE);
                                            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            System.out.println("Error "+ e.getMessage());
                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                            binding.progressBar.setVisibility(View.GONE);
                                            binding.submitBtn.setVisibility(View.VISIBLE);
                                            Intent intent=new Intent(SignUp.this,OtpVerify.class);
                                            intent.putExtra("mobile",binding.mobile.getEditText().getText().toString());
                                            intent.putExtra("backendotp",s);
                                            startActivity(intent);

                                        }
                                    })          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);


                    System.out.println("Mobile no. : "+binding.mobile.getEditText().getText().toString());
                }

            }
        });
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this,MainActivity.class));
            }
        });

    }
}
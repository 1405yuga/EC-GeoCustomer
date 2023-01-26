package com.example.ec_geocustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ec_geocustomer.data.Profile;
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
                if(binding.name.getEditText().getText().toString().isEmpty()){
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                    binding.name.setError("Enter your name");
                    return;
                }
                if(binding.addLine1.getEditText().getText().toString().isEmpty()){
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                    binding.addLine1.setError("Enter your address");
                    return;
                }
                if(binding.city.getEditText().getText().toString().isEmpty()){
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                    binding.city.setError("Enter your city/town");
                    return;
                }
                //checking if mobile empty
                if(binding.mobile.getEditText().getText().toString().isEmpty()){
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                    binding.mobile.setError("Enter mobile number");
                    return;
                }
                else if(binding.mobile.getEditText().getText().toString().length()!=10){
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                    binding.mobile.setError("Enter valid mobile number");
                    return;
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
                                            Profile profile=new Profile(binding.name.getEditText().getText().toString(),
                                                    binding.addLine1.getEditText().getText().toString()+" "+binding.addLine2.getEditText().getText().toString(),
                                                    binding.city.getEditText().getText().toString(),
                                                    Long.parseLong(binding.mobile.getEditText().getText().toString()));
                                            intent.putExtra("profile",profile);
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
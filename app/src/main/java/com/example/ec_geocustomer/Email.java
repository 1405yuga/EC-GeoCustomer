package com.example.ec_geocustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ec_geocustomer.databinding.ActivityEmailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Email extends AppCompatActivity {

    ActivityEmailBinding binding;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.submitBtn.setVisibility(View.INVISIBLE);

                if(binding.email.getEditText().getText().toString().trim().isEmpty() || !binding.email.getEditText().getText().toString().contains("@")){
                    binding.email.getEditText().setError("Enter valid email");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                }
                if(binding.password1.getEditText().getText().toString().trim().length()<6){
                    binding.password1.setError("Password must be atleast 6 characters");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                }
                if(binding.password2.getEditText().getText().toString().trim().length()<6){
                    binding.password2.setError("Password must be atleast 6 characters");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                }
               
                if(!binding.password2.getEditText().getText().toString().trim().equals(binding.password1.getEditText().getText().toString().trim())){
                    binding.password2.setError("Password doesn't match");
                    binding.password2.setError("Password doesn't match");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                }
                else{
                    
                    //create account
                    FirebaseAuth mAuth=FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(binding.email.getEditText().getText().toString(),binding.password1.getEditText().getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        // send verification link
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(Email.this,"Email verification link is sent to "+binding.email.getEditText().getText().toString(),Toast.LENGTH_SHORT).show();

                                                    // TODO: 27-01-2023 Add profile in firestore
                                                    startActivity(new Intent(Email.this,MainActivity.class));
                                                    finish();
                                                    binding.progressBar.setVisibility(View.INVISIBLE);
                                                    binding.submitBtn.setVisibility(View.VISIBLE);
                                                }
                                                else{
                                                    Toast.makeText(Email.this,"Error: Failed to send email verification link "+task.getException(),Toast.LENGTH_SHORT).show();
                                                    binding.progressBar.setVisibility(View.INVISIBLE);
                                                    binding.submitBtn.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });

                                    }
                                    else{
                                        Toast.makeText(Email.this,"Account creation failed!!",Toast.LENGTH_SHORT).show();
                                        System.out.println("Error"+task.getException().getMessage());
                                        binding.progressBar.setVisibility(View.INVISIBLE);
                                        binding.submitBtn.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Email.this,MainActivity.class));
                finish();
            }
        });
    }
}
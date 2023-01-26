package com.example.ec_geocustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ec_geocustomer.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.email.getEditText().getText().toString().isEmpty()){
                    binding.email.getEditText().setError("Enter valid email");
                }
                if(binding.passtxt.getEditText().getText().toString().isEmpty()){
                    binding.email.getEditText().setError("Enter valid password");
                }
                else{
                    //signin
                    FirebaseAuth fAuth=FirebaseAuth.getInstance();
                    fAuth.signInWithEmailAndPassword(binding.email.getEditText().getText().toString(),binding.passtxt.getEditText().getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        if(fAuth.getCurrentUser().isEmailVerified()){
                                            Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MainActivity.this,Menus.class));
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(MainActivity.this, "Get email verified", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "You don't have account!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

            }
        });
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SignUp.class));
            }
        });
    }
}
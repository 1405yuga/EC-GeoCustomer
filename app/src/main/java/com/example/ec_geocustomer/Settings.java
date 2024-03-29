package com.example.ec_geocustomer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ec_geocustomer.data.FiresStoreTableConstants;
import com.example.ec_geocustomer.data.Profile;
import com.example.ec_geocustomer.databinding.DialogEditProfileBinding;
import com.example.ec_geocustomer.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;


public class Settings extends Fragment {


    FragmentSettingsBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FiresStoreTableConstants constants;
    Profile profile, document;
    String getbackendotp;
    String new_email;

    public Settings() {
        // Required empty public constructor
    }

    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.bind(inflater.inflate(R.layout.fragment_settings, container, false));
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        constants = new FiresStoreTableConstants();
        final String email = firebaseAuth.getCurrentUser().getEmail();
        // get details and displAy profile
        firebaseFirestore.collection(constants.getCustomer()).document(email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        profile = documentSnapshot.toObject(Profile.class);
                        Log.d(TAG, "profile:" + profile);
                        binding.name.setText(profile.getName());
                        binding.address.setText(profile.getAddress());
                        binding.city.setText(profile.getCity());
                        binding.mobile.setText(profile.getMobile() + "");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "failed " + e.getMessage());
                    }
                });


        binding.email.setText(firebaseAuth.getCurrentUser().getEmail());

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().onBackPressed();
            }
        });
        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                DialogEditProfileBinding dialogBinding = DialogEditProfileBinding.inflate(getLayoutInflater());
                dialog.setContentView(dialogBinding.getRoot());
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();
                dialog.setCancelable(false);
                dialogBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialogBinding.name.getEditText().setText(profile.getName());
                dialogBinding.addLine1.getEditText().setText(profile.getAddress());
                dialogBinding.city.getEditText().setText(profile.getCity());
                dialogBinding.mobile.getEditText().setText(profile.getMobile().toString());

                dialogBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firebaseFirestore = FirebaseFirestore.getInstance();

                        //name
                        if (!dialogBinding.name.getEditText().getText().toString().trim().equals(profile.getName())) {
                            firebaseFirestore.collection(constants.getCustomer()).document(email).update("name", dialogBinding.name.getEditText().getText().toString());
                            profile.setName(dialogBinding.name.getEditText().getText().toString());
                            binding.name.setText(profile.getName());
                            Toast.makeText(getContext(), "Updated profile sucessfully", Toast.LENGTH_SHORT).show();
                        }
                        //address
                        if (!dialogBinding.addLine1.getEditText().getText().toString().trim().equals(profile.getAddress())) {
                            firebaseFirestore.collection(constants.getCustomer()).document(email).update("address", dialogBinding.addLine1.getEditText().getText().toString());
                            profile.setAddress(dialogBinding.addLine1.getEditText().getText().toString());
                            binding.address.setText(profile.getAddress());
                            Toast.makeText(getContext(), "Updated profile sucessfully", Toast.LENGTH_SHORT).show();
                        }

                        //city
                        if (!dialogBinding.city.getEditText().getText().toString().trim().equals(profile.getCity())) {
                            firebaseFirestore.collection(constants.getCustomer()).document(email).update("city", dialogBinding.city.getEditText().getText().toString());
                            profile.setCity(dialogBinding.city.getEditText().getText().toString());
                            binding.city.setText(profile.getCity());
                            Toast.makeText(getContext(), "Updated profile sucessfully", Toast.LENGTH_SHORT).show();
                        }
                        //mobile changed
                        if (!dialogBinding.mobile.getEditText().getText().toString().trim().equals(profile.getMobile().toString())) {
                            dialogBinding.otp.setVisibility(View.VISIBLE);
                            dialogBinding.verifyBtn.setVisibility(View.VISIBLE);
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            PhoneAuthOptions options =
                                    PhoneAuthOptions.newBuilder(mAuth)
                                            .setPhoneNumber("+91" + dialogBinding.mobile.getEditText().getText().toString())       // Phone number to verify
                                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                            .setActivity(getActivity())                 // Activity (for callback binding)
                                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                @Override
                                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                                    dialogBinding.progressBar.setVisibility(View.GONE);
                                                    dialogBinding.submitBtn.setVisibility(View.VISIBLE);
                                                }

                                                @Override
                                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                                    dialogBinding.progressBar.setVisibility(View.INVISIBLE);
                                                    dialogBinding.submitBtn.setVisibility(View.VISIBLE);
                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                    System.out.println("Error " + e.getMessage());
                                                }

                                                @Override
                                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                    dialogBinding.progressBar.setVisibility(View.GONE);
                                                    dialogBinding.submitBtn.setVisibility(View.VISIBLE);
                                                    getbackendotp = s;
                                                }
                                            })          // OnVerificationStateChangedCallbacks
                                            .build();
                            PhoneAuthProvider.verifyPhoneNumber(options);

                            //check otp
                            dialogBinding.verifyBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //check if entered otp is correct;
                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(getbackendotp, dialogBinding.otp.getEditText().getText().toString());
                                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Otp verified successfully !", Toast.LENGTH_SHORT).show();
                                                dialogBinding.progressBar.setVisibility(View.INVISIBLE);
                                                dialogBinding.verifyBtn.setVisibility(View.GONE);
                                                dialogBinding.otp.setVisibility(View.GONE);
                                                firebaseFirestore.collection(constants.getCustomer()).document(email).update("mobile", Long.parseLong(dialogBinding.mobile.getEditText().getText().toString().trim()));
                                                Toast.makeText(getContext(), "Updated profile sucessfully", Toast.LENGTH_SHORT).show();
                                                profile.setMobile(Long.parseLong(dialogBinding.mobile.getEditText().getText().toString()));
                                                binding.mobile.setText(profile.getMobile().toString());

                                            } else {
                                                dialogBinding.otp.setError("Enter correct otp");
                                                dialogBinding.progressBar.setVisibility(View.INVISIBLE);
                                            }

                                        }
                                    });
                                }
                            });


                        }

                    }
                });
            }
        });


        return binding.getRoot();
    }
}
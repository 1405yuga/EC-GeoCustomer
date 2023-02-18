package com.example.ec_geocustomer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.ec_geocustomer.data.FiresStoreTableConstants;
import com.example.ec_geocustomer.data.Profile;
import com.example.ec_geocustomer.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {


    FragmentSettingsBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FiresStoreTableConstants constants;

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
        constants=new FiresStoreTableConstants();
        final String email = firebaseAuth.getCurrentUser().getEmail();
        // get details and displAy profile
        firebaseFirestore.collection(constants.getCustomer()).document(email).collection(constants.getCustomerProfile()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        DocumentSnapshot documentSnapshot = documentSnapshotList.get(0);
                        Profile profile = documentSnapshot.toObject(Profile.class);
                        binding.name.setText(profile.getName());
                        binding.address.setText(profile.getAddress());
                        binding.city.setText(profile.getCity());
                        binding.mobile.setText(profile.getMobile() + "");
                    }
                });


        binding.email.setText(firebaseAuth.getCurrentUser().getEmail());

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 27-01-2023 edit profile
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().onBackPressed();
            }
        });
        return binding.getRoot();
    }
}
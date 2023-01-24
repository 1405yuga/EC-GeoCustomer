package com.example.ec_geocustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.ec_geocustomer.databinding.ActivityMenusBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class Menus extends AppCompatActivity {

    ActivityMenusBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMenusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ReplaceFragment(new SearchViewFragment());
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.search:
                        ReplaceFragment(new SearchViewFragment());
                        break;
                    case R.id.scanner:
                        ReplaceFragment(new ScannerFragment());
                        break;
                    case R.id.logout:FirebaseAuth.getInstance().signOut();
                                     startActivity(new Intent(Menus.this,MainActivity.class));
                                     finish();
                                     break;

                }
                return true;
            }
        });
    }

    private void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
}
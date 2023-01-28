package com.example.ec_geocustomer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ec_geocustomer.databinding.FragmentSearchViewBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SearchViewFragment extends Fragment {

    FragmentSearchViewBinding binding;
    boolean newPoints=false;
    ArrayList<LatLng> locationArrayList = new ArrayList<>();
    LatLng sydney = new LatLng(-34, 151);
    LatLng TamWorth = new LatLng(-31.083332, 150.916672);
    LatLng NewCastle = new LatLng(-32.916668, 151.750000);
    LatLng Brisbane = new LatLng(-27.470125, 153.021072);
    private OnMapReadyCallback callback = new OnMapReadyCallback() {



        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            if(newPoints){
                for(int i=0;i<locationArrayList.size();i++){
                    googleMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Marker"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList.get(i)));
                }
            }
            else{
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding= FragmentSearchViewBinding.bind(inflater.inflate(R.layout.fragment_search_view, container, false));
        /*
        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // on below line we are adding our
                // locations in our array list.
                locationArrayList.add(sydney);
                locationArrayList.add(TamWorth);
                locationArrayList.add(NewCastle);
                locationArrayList.add(Brisbane);
                newPoints=true;
                onViewCreated(binding.getRoot(),savedInstanceState);

            }
        });
        */
        binding.searchView.clearFocus();;
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO: 28-01-2023 display shops

                locationArrayList.add(sydney);
                locationArrayList.add(TamWorth);
                locationArrayList.add(NewCastle);
                locationArrayList.add(Brisbane);
                newPoints=true;


                binding.searchView.clearFocus();
                onViewCreated(binding.getRoot(),savedInstanceState);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO: 28-01-2023 suggestions 
                return true;
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}
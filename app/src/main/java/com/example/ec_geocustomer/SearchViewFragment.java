package com.example.ec_geocustomer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

import com.example.ec_geocustomer.data.Availability;
import com.example.ec_geocustomer.data.FiresStoreTableConstants;
import com.example.ec_geocustomer.data.Profile;
import com.example.ec_geocustomer.data.Shop;
import com.example.ec_geocustomer.data.ShopProfile;
import com.example.ec_geocustomer.databinding.FragmentSearchViewBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchViewFragment extends Fragment {

    private static String barcode;
    FragmentSearchViewBinding binding;
    boolean newPoints = false;
    HashMap<String,LatLng> shopsWithId = new HashMap<>();
    LatLng sydney = new LatLng(-34, 151);
    LatLng TamWorth = new LatLng(-31.083332, 150.916672);
    LatLng NewCastle = new LatLng(-32.916668, 151.750000);
    LatLng Brisbane = new LatLng(-27.470125, 153.021072);
    HashMap<String, String> list = new HashMap<>();
    SimpleCursorAdapter cursorAdapter;
    FirebaseFirestore firebaseFirestore, fStore;
    FirebaseAuth firebaseAuth;
    FiresStoreTableConstants constants = new FiresStoreTableConstants();
    HashMap<String,Availability> ShopsList=new HashMap<>();
    String itemBarcodeSearched = null;

    SupportMapFragment mapFragment;
    GoogleMap map;

    Runnable runnable;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            map = googleMap;
            Log.d(TAG, "onMapReady called");
            if (newPoints) {
                for (LatLng latLng : shopsWithId.values()) {
                    map.addMarker(new MarkerOptions().position(latLng).title("new!!"));
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Shop shop = (Shop) marker.getTag();
                        Log.d(TAG,"on CLICK "+" shop obj"+shop.getShopname());
// TODO: 07-03-2023 create dialog

                        return false;
                    }
                });

            } else {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseFirestore = FirebaseFirestore.getInstance();
                final String email = firebaseAuth.getCurrentUser().getEmail();
                firebaseFirestore.collection(constants.getCustomer()).document(email)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Profile profile = documentSnapshot.toObject(Profile.class);
                                Log.d(TAG, "profile:" + profile);
                                LatLng you = new LatLng(profile.getLatitude(), profile.getLongitude());
                                map.addMarker(new MarkerOptions().position(you).title("You!!"));
                                map.moveCamera(CameraUpdateFactory.newLatLng(you));
                            }
                        })
                        .addOnFailureListener(e -> e.printStackTrace());
            }
        }


    };


    private boolean isSuggestionClicked = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentSearchViewBinding.bind(inflater.inflate(R.layout.fragment_search_view, container, false));

        // get itemnames
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FiresStoreTableConstants constants = new FiresStoreTableConstants();
        firebaseFirestore.collection(constants.getBarcode())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documents) {
                        list.put(documentSnapshot.getId(), documentSnapshot.getString(constants.getBarcodeName()));
                    }
                });
        //set suggestion adapter
        int[] to = new int[]{R.id.searchItemID};
        String[] from = new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1};
        cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.suggestion_list, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        binding.searchView.setSuggestionsAdapter(cursorAdapter);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isSuggestionClicked) {
                    // get barcode of item clicked
                    Log.d(TAG, "isSuggestion clicked !");
                    barcode = getBarcode(query);
                    if (barcode != null) {
                        fStore = FirebaseFirestore.getInstance();
                        fStore.collection(constants.getOwner())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        // get list of shops
                                        List<DocumentSnapshot> ids = queryDocumentSnapshots.getDocuments();
                                        Log.d(TAG, "OWNER IDS " + ids.size());
                                        for (DocumentSnapshot id : ids) {
                                            fStore.collection(constants.getOwner()).document(id.getId()).collection(constants.getOwnerAvailability())
                                                    .whereGreaterThan(constants.getOwnerQuantity(), 0)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            List<DocumentSnapshot> barcodeList = queryDocumentSnapshots.getDocuments();
                                                            for (DocumentSnapshot documentSnapshot : barcodeList) {
                                                                if (documentSnapshot.getId().equals(barcode)) {
                                                                    Log.d(TAG,"avail discount "+documentSnapshot.getDouble(constants.getOwnerDiscount()));
                                                                    Availability availability=new Availability(documentSnapshot.getLong(constants.getOwnerQuantity()),documentSnapshot.getDouble(constants.getOwnerDiscount()));
                                                                    ShopsList.put(id.getId(),availability);
                                                                }
                                                            }
                                                            addShopsLatnLong(ShopsList);
                                                            Log.d(TAG, "List of shops:" + ShopsList);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "Error: " + e.getMessage());
                                                        }
                                                    });
                                        }
                                    }
                                })

                                .addOnFailureListener(e -> e.printStackTrace());
                        newPoints = true;
                    }
                } else {
                    Toast.makeText(getActivity(), "Select products from suggestion", Toast.LENGTH_SHORT).show();
                }
                binding.searchView.clearFocus();
                onViewCreated(binding.getRoot(), savedInstanceState);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // suggestions from firebase
                MatrixCursor cursor = new MatrixCursor(new String[]{
                        BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1
                });
                ArrayList<String> valueList = new ArrayList<String>(list.values());
                for (int j = 0; j < valueList.size(); j++) {
                    if (valueList.get(j).toLowerCase().startsWith(newText.toLowerCase()))
                        cursor.addRow(new Object[]{j, valueList.get(j)});
                }
                cursorAdapter.changeCursor(cursor);
                return false;
            }
        });

        binding.searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor1 = (Cursor) cursorAdapter.getItem(position);
                @SuppressLint("Range") String item = cursor1.getString(cursor1.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                isSuggestionClicked = true;
                binding.searchView.setQuery(item, true);
                return true;
            }
        });
        return binding.getRoot();
    }

    private void addShopsLatnLong(HashMap<String, Availability> shopsList) {
        newPoints = true;

        if (shopsList.size() > 0) {

            Log.d(TAG, "addshops called");
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            for (Map.Entry<String,Availability> shopId : shopsList.entrySet()) {
                firestore.collection(constants.getOwner()).document(shopId.getKey())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            ShopProfile profile = documentSnapshot.toObject(ShopProfile.class);
                            LatLng latLng = new LatLng(profile.getLatitude(), profile.getLongitude());
//                            locationArrayList.add(latLng);
                            shopsWithId.put(shopId.getKey(),latLng);
                            Shop shop = new Shop(profile.getShopname(),profile.getOwnername(),profile.getAddress(),profile.getCity(),
                                    profile.getMobile(),profile.getLatitude(),profile.getLongitude(),shopId.getValue().getQuantity(), shopId.getValue().getDiscount(), shopId.getKey());
                            Marker m=map.addMarker(new MarkerOptions().
                                    position(latLng).
                                    title(profile.getShopname()).
                                    icon(BitmapDescriptorFactory.defaultMarker(30))
                            );
                            m.setTag(shop);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            map.animateCamera(CameraUpdateFactory.zoomIn());

                        })
                        .addOnFailureListener(e -> Log.d(TAG, "error: " + e.getMessage()));
            }
        }
    }

    private String getBarcode(String query) {
        //get barcode
        for (Map.Entry<String, String> entry : list.entrySet()) {
            if (entry.getValue().trim().equals(query.trim())) {
                itemBarcodeSearched = entry.getKey();
                return itemBarcodeSearched;
            }
        }
        return itemBarcodeSearched;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }


}
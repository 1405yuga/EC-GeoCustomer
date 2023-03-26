package com.example.ec_geocustomer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ec_geocustomer.data.Availability;
import com.example.ec_geocustomer.data.FiresStoreTableConstants;
import com.example.ec_geocustomer.data.ItemBarcode;
import com.example.ec_geocustomer.data.MarkerTagShopItembarcode;
import com.example.ec_geocustomer.data.Profile;
import com.example.ec_geocustomer.data.Shop;
import com.example.ec_geocustomer.data.ShopProfile;
import com.example.ec_geocustomer.databinding.DisplayProductDialogBinding;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchViewFragment extends Fragment {

    private static String barcode;
    FragmentSearchViewBinding binding;
    boolean newPoints = false;
    HashMap<String, LatLng> shopsWithId = new HashMap<>();
    HashMap<String, String> list = new HashMap<>();
    SimpleCursorAdapter cursorAdapter;
    FirebaseFirestore firebaseFirestore, fStore;
    FirebaseAuth firebaseAuth;
    FiresStoreTableConstants constants = new FiresStoreTableConstants();
    HashMap<String, Availability> ShopsList = new HashMap<>();
    String itemBarcodeSearched = null, subCategory = null;

    SupportMapFragment mapFragment;
    GoogleMap map;

    ItemBarcode itemBarcode;
    LatLng you;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            map = googleMap;
            Log.d(TAG, "onMapReady called");
            map.clear();
            if (newPoints) {
                map.addMarker(new MarkerOptions().position(you).title("You!!"));
                map.moveCamera(CameraUpdateFactory.newLatLng(you));
                for (LatLng latLng : shopsWithId.values()) {
                    map.addMarker(new MarkerOptions().position(latLng).title("new!!"));
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                map.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                    @Override
                    public void onInfoWindowLongClick(@NonNull Marker marker) {
                        Log.d(TAG, "onInfoWindowLongClick " + marker.getTitle());
                        MarkerTagShopItembarcode tag = (MarkerTagShopItembarcode) marker.getTag();
                        if (tag != null) {
                            Shop shop = tag.getShop();
                            ItemBarcode itemBarcode1 = tag.getItemBarcode();
                            if (shop != null && itemBarcode1 != null) {
                                Log.d(TAG, "on CLICK " + " shop obj" + shop.getShopname());
//                      create dialog for shop
                                Dialog dialog = new Dialog(getContext());
                                DisplayProductDialogBinding dialogBinding = DisplayProductDialogBinding.inflate(getLayoutInflater());
                                dialog.setContentView(dialogBinding.getRoot());
                                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                dialog.show();
                                dialogBinding.productName.setText(itemBarcode1.getName());
                                dialogBinding.size.setText(itemBarcode1.getSize());
                                dialogBinding.oldPrice.setText(itemBarcode1.getMrp().toString());
                                subCategory = itemBarcode1.getSubCategory();
                                Double d = itemBarcode1.getMrp();
                                final Double newPrice = d * (100 - shop.getDiscount()) / 100;
                                dialogBinding.price.setText(newPrice.toString());
                                //set image
                                Glide
                                        .with(getActivity())
                                        .load(itemBarcode1.getUrl())
                                        .centerCrop()
                                        .into(dialogBinding.productImage);
                                dialogBinding.shopname.setText(shop.getShopname());
                                dialogBinding.discount.setText(shop.getDiscount().toString());
                                dialogBinding.qtyAvail.setText(shop.getQuantity().toString());
                                dialogBinding.buyBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //  check qty n available
                                        if (dialogBinding.qty.getEditText().getText().toString().isEmpty() || Integer.parseInt(dialogBinding.qty.getEditText().getText().toString()) <= 0
                                                || shop.getQuantity() < Integer.parseInt(dialogBinding.qty.getEditText().getText().toString())) {
                                            dialogBinding.qty.setError("Enter proper quantity required");
                                            return;
                                        }
                                        if (shop.getQuantity() >= Integer.parseInt(dialogBinding.qty.getEditText().getText().toString())) {
                                            dialog.dismiss();

                                            //  go to payment activity req:shop,qty_purchased,item/barcode profile
                                            Intent intent = new Intent(getActivity(), PaymentActivity.class);
                                            intent.putExtra("shop", shop);
                                            intent.putExtra("qty_purchased", dialogBinding.qty.getEditText().getText().toString());
                                            intent.putExtra("itembarcode", itemBarcode1);
                                            startActivity(intent);
                                        }

                                    }
                                });
                            }
                        }
                    }
                });

            } else {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseFirestore = FirebaseFirestore.getInstance();
                final String email = firebaseAuth.getCurrentUser().getEmail();
                if (email == null || email.trim().isEmpty()) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
                firebaseFirestore.collection(constants.getCustomer())
                        .document(email)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Profile profile = documentSnapshot.toObject(Profile.class);
                                Log.d(TAG, "profile:" + profile);
                                you = new LatLng(profile.getLatitude(), profile.getLongitude());
                                map.addMarker(new MarkerOptions().position(you).title("You!!"));
                                map.moveCamera(CameraUpdateFactory.newLatLng(you));
                            }
                        })
                        .addOnFailureListener(e -> e.printStackTrace());
            }
        }


    };


    private boolean isSuggestionClicked = false;

    public SearchViewFragment() {
    }

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
                    shopsWithId = new HashMap<>();
                    ShopsList = new HashMap<>();
                    barcode = getBarcode(query);
                    Log.d(TAG, "barcode " + barcode);
                    if (barcode != null) {

                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        firebaseFirestore.collection(constants.getBarcode()).document(barcode).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        itemBarcode = new ItemBarcode(documentSnapshot.getId(), documentSnapshot.getString(constants.getBarcodeName()),
                                                documentSnapshot.getString(constants.getBarcodeCategory()), documentSnapshot.getString(constants.getBarcodeSubCatgeory()),
                                                documentSnapshot.getString(constants.getBarcodeSize()), documentSnapshot.getString(constants.getBarcodeUrl()),
                                                documentSnapshot.getString(constants.getBarcodeBrand()), documentSnapshot.getDouble(constants.getBarcodePrice()));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
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
                                                                    Log.d(TAG, "avail discount " + documentSnapshot.getDouble(constants.getOwnerDiscount()));
                                                                    Availability availability = new Availability(documentSnapshot.getLong(constants.getOwnerQuantity()), documentSnapshot.getDouble(constants.getOwnerDiscount()));
                                                                    ShopsList.put(id.getId(), availability);
                                                                }
                                                            }
                                                            addShopsLatnLong(itemBarcode, ShopsList);
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

    private void addShopsLatnLong(ItemBarcode itemBarcode, HashMap<String, Availability> shopsList) {
        newPoints = true;

        if (shopsList.size() > 0) {

            Log.d(TAG, "addshops called");
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            for (Map.Entry<String, Availability> shopId : shopsList.entrySet()) {
                firestore.collection(constants.getOwner()).document(shopId.getKey())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            ShopProfile profile = documentSnapshot.toObject(ShopProfile.class);
                            LatLng latLng = new LatLng(profile.getLatitude(), profile.getLongitude());
//                            locationArrayList.add(latLng);
                            shopsWithId.put(shopId.getKey(), latLng);
                            Double d = itemBarcode.getMrp();
                            final Double newPrice = d * (100 - shopId.getValue().getDiscount()) / 100;
                            Shop shop = new Shop(profile.getShopname(), profile.getOwnername(), profile.getAddress(), profile.getCity(), profile.getUpiId(),
                                    profile.getMobile(), profile.getLatitude(), profile.getLongitude(), shopId.getValue().getQuantity(), shopId.getValue().getDiscount(), shopId.getKey());
                            Marker m = map.addMarker(new MarkerOptions().
                                    position(latLng).
                                    title(profile.getShopname())
                                    .snippet(getContext().getString(R.string.Rs) + newPrice)
                                    .icon(BitmapDescriptorFactory.defaultMarker(30))
                            );
                            m.showInfoWindow();
                            MarkerTagShopItembarcode tag = new MarkerTagShopItembarcode(itemBarcode, shop);
                            if (tag != null) {
                                m.setTag(tag);
                            }
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
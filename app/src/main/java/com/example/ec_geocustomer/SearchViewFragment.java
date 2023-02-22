package com.example.ec_geocustomer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

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

import com.example.ec_geocustomer.data.FiresStoreTableConstants;
import com.example.ec_geocustomer.data.Profile;
import com.example.ec_geocustomer.databinding.FragmentSearchViewBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

    FragmentSearchViewBinding binding;
    boolean newPoints = false;
    ArrayList<LatLng> locationArrayList = new ArrayList<>();
    LatLng sydney = new LatLng(-34, 151);
    LatLng TamWorth = new LatLng(-31.083332, 150.916672);
    LatLng NewCastle = new LatLng(-32.916668, 151.750000);
    LatLng Brisbane = new LatLng(-27.470125, 153.021072);
    HashMap<String,String> list = new HashMap<>();
    SimpleCursorAdapter cursorAdapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FiresStoreTableConstants constants;
    private boolean isSuggestionClicked=false;

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

            if (newPoints) {
                for (int i = 0; i < locationArrayList.size(); i++) {
                    googleMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Marker"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList.get(i)));
                }
            } else {
                firebaseAuth=FirebaseAuth.getInstance();
                firebaseFirestore=FirebaseFirestore.getInstance();
                constants=new FiresStoreTableConstants();
                final String email = firebaseAuth.getCurrentUser().getEmail();
                firebaseFirestore.collection(constants.getCustomer()).document(email).collection(constants.getCustomerProfile()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                                DocumentSnapshot documentSnapshot = documentSnapshotList.get(0);
                                Profile profile = documentSnapshot.toObject(Profile.class);
                                LatLng you = new LatLng(profile.getLatitude(), profile.getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(you).title("You!!"));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(you));
                            }
                        });
                /*
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                 */
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchViewBinding.bind(inflater.inflate(R.layout.fragment_search_view, container, false));

        // get itemnames
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        FiresStoreTableConstants constants=new FiresStoreTableConstants();
        firebaseFirestore.collection(constants.getBarcode())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documents=queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot documentSnapshot :documents){
                            list.put(documentSnapshot.getId(),documentSnapshot.getString(constants.getBarcodeName()));
                        }

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
                // TODO: 28-01-2023 display shops

                locationArrayList.add(sydney);
                locationArrayList.add(TamWorth);
                locationArrayList.add(NewCastle);
                locationArrayList.add(Brisbane);

                if(isSuggestionClicked){
                    // TODO: 18-02-2023  clicked from items in barcodelist
                    Log.d(TAG,"isSuggestion clicked !");
                    getShops(query);
                    newPoints = true;

                }
                else{
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
                isSuggestionClicked=true;
                binding.searchView.setQuery(item, true);
                return true;
            }
        });
        return binding.getRoot();
    }

    String itemBarcodeSearched = null;
    private void getShops(String query) {

        Log.d(TAG,"getShops() called"+query);
        //get barcode
        for(Map.Entry<String, String> entry: list.entrySet()) {
            Log.d(TAG,"value :"+entry.getValue());
            if(entry.getValue().trim().equals(query.trim())) {
                itemBarcodeSearched= entry.getKey();
                break;
            }
        }

        if(itemBarcodeSearched!=null){
            firebaseFirestore=FirebaseFirestore.getInstance();
            firebaseFirestore.collection(constants.getOwner())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d(TAG,"getOwners called");
                            List<DocumentSnapshot> ownerIds=queryDocumentSnapshots.getDocuments();
                            //go through each owner
                            for(DocumentSnapshot ownerId:ownerIds){
                                //FirebaseFirestore firestore=FirebaseFirestore.getInstance();
                                Log.d(TAG,"Owner email "+ownerId.getId());
                                //check for availability
                                /*
                                firestore.collection(constants.getOwner()).document(ownerId.getId()).collection(constants.getOwnerAvailability())
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                Log.d(TAG,"Availability "+queryDocumentSnapshots.getDocuments().get(0).getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.d(TAG,"Error !"+e.getMessage());
                                            }
                                        });

                                 */

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG,e.getMessage());
                        }
                    });
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
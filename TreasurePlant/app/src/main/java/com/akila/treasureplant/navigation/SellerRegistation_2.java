package com.akila.treasureplant.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.akila.treasureplant.R;
import com.akila.treasureplant.SellerMainActivity;
import com.akila.treasureplant.Treasure_Plant_Seller;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Random;

public class SellerRegistation_2 extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    private static final int LOCATION_REQUEST_CODE = 100;

    private LatLng userLatLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_registation_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id",null);

        SupportMapFragment supportMapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.sellerSignUpMapFrameLayout, supportMapFragment);
        fragmentTransaction.commit();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {

                googleMap = map;

                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                }

            }
        });

        view.findViewById(R.id.sellerCreateAccountButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int sellerId = new Random().nextInt(1000000000);

                HashMap<String, Object> seller = new HashMap<>();
                seller.put("address1", SellerRegistation_1.data[0]);
                seller.put("address2", SellerRegistation_1.data[1]);
                seller.put("mobile", SellerRegistation_1.data[2]);
                seller.put("city_id", SellerRegistation_1.data[3]);
                seller.put("id", sellerId);
                seller.put("user_id", Integer.parseInt(id));
                seller.put("isBlock", false);
                seller.put("location", userLatLng);

                firestore.collection("seller")
                        .add(seller).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getActivity(), "Account Created Successfully", Toast.LENGTH_SHORT).show();

                                SharedPreferences sellerSharedPreferences = getActivity().getSharedPreferences("seller", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sellerSharedPreferences.edit();
                                editor.putBoolean("isGoogle", false);
                                editor.putString("seller_id", String.valueOf(sellerId));
                                editor.apply();
                                startActivity(new Intent(getActivity(), SellerMainActivity.class));
                            }
                        });
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("app30Log", "Location Permission Granted");
                if (googleMap != null) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                }
                getCurrentLocation();
            } else {
                Log.i("app30Log", "Location Permission Denied");
            }
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("logTP", userLatLng.toString());
                    googleMap.addMarker(new MarkerOptions().position(userLatLng).title("You are here"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                } else {
                    Log.i("app30Log", "Failed to get current location.");
                }
            });
        }
    }
}


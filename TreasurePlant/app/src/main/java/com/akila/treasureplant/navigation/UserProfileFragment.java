package com.akila.treasureplant.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akila.treasureplant.R;
import com.akila.treasureplant.SignInActivity;
import com.akila.treasureplant.model.DataClearer;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class UserProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
             TextView textView = view.findViewById(R.id.user_profile_name);
             TextView textView1 = view.findViewById(R.id.user_profile_email);
             ImageView imageView = view.findViewById(R.id.user_profile_image);

             textView.setText(firebaseUser.getDisplayName());
             textView1.setText(firebaseUser.getEmail());
            Glide.with(this).load(firebaseUser.getPhotoUrl().toString()).into(imageView);
        }

        view.findViewById(R.id.user_profile_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_1, ProfileUpdateFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        view.findViewById(R.id.user_profile_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_1, AddressFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        view.findViewById(R.id.user_profile_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_1, OrderHistoryFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        view.findViewById(R.id.user_profile_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                sharedPreferences.edit().remove("id").apply();
                DataClearer.clearAppData(getContext());
                startActivity(new Intent(getActivity(), SignInActivity.class));
//                getActivity().finish();
            }
        });

    }
}
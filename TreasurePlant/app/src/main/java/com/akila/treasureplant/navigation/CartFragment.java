package com.akila.treasureplant.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akila.treasureplant.CheckoutFragment;
import com.akila.treasureplant.MainActivity;
import com.akila.treasureplant.R;
import com.akila.treasureplant.adapter.CartAdapter;
import com.akila.treasureplant.model.CartItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartFragment extends Fragment {

    public static String cartDocumentId;
    public static String cartTotalPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id",null);

        ArrayList<CartItem> cartItemArrayList = new ArrayList<>();
        CartAdapter cartAdapter = new CartAdapter(cartItemArrayList, getActivity());

        TextView total = view.findViewById(R.id.cartTotal);
        int[] totalValue = {0};

        firestore.collection("cart")
                .where(Filter.equalTo("user_id", Integer.parseInt(id)))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();

                        if (documentSnapshotList.isEmpty()) {
                            //cart empty
                            Toast.makeText(getActivity().getApplicationContext(), "Your cart is Empty", Toast.LENGTH_LONG).show();
                        } else {
                            //cart not empty
                            DocumentSnapshot documentSnapshot = documentSnapshotList.get(0);
                            ArrayList<HashMap> productArray = (ArrayList<HashMap>) documentSnapshot.get("product_array");

                            for (HashMap<String, Object> hashMap : productArray) {

                                String id = String.valueOf(hashMap.get("id"));
                                String title = String.valueOf(hashMap.get("title"));
                                String price = String.valueOf(hashMap.get("price"));
                                String quantity = String.valueOf(hashMap.get("quantity"));

                                totalValue[0] = totalValue[0] + Integer.parseInt(price) * Integer.parseInt(quantity);

                                cartItemArrayList.add(new CartItem(id, title, price, quantity));
                            }

                            cartDocumentId = documentSnapshot.getId();
                            cartTotalPrice = String.valueOf(totalValue[0]);

                            cartAdapter.notifyDataSetChanged();
                            total.setText(getString(R.string.cart_text1) + " " + totalValue[0]);
                        }
                    }
                });

        RecyclerView recyclerView = view.findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(cartAdapter);

        Log.d("logTP", String.valueOf(totalValue[0]));

        view.findViewById(R.id.cartCheckoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_1, CheckoutFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });


    }
}
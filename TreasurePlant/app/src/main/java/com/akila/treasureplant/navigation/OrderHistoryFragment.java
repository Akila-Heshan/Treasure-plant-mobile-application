package com.akila.treasureplant.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akila.treasureplant.R;
import com.akila.treasureplant.adapter.OrderHistoryAdapter;
import com.akila.treasureplant.model.OrderHistoryItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderHistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("id",null);

        ArrayList<OrderHistoryItem> orderHistoryItems = new ArrayList<>();
        OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(orderHistoryItems, getActivity());

        firestore.collection("order").whereEqualTo("user_id", user_id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();

                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            ArrayList<HashMap> hashMaps = (ArrayList<HashMap>) documentSnapshot.get("order_array");

                            for (HashMap hashMap : hashMaps) {
                                String productId = (String) hashMap.get("title");
                                String price = (String) hashMap.get("price");
                                String quantity = (String) hashMap.get("quantity");

                                firestore.collection("product").document(productId)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                String title = (String) task.getResult().get("title");
                                                firestore.collection("review")
                                                        .where(Filter.and(
                                                                Filter.equalTo("user_id", user_id),
                                                                Filter.equalTo("product_id", (String) hashMap.get("title"))
                                                        )).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                int review = 0;

                                                                if (!task.getResult().getDocuments().isEmpty()) {
                                                                    review = Integer.parseInt((String) task.getResult().getDocuments().get(0).get("review"));
                                                                }

                                                                orderHistoryItems.add(new OrderHistoryItem(productId, title, price, quantity, review));
                                                                orderHistoryAdapter.notifyDataSetChanged();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                    }
                });

        RecyclerView recyclerView = view.findViewById(R.id.order_history_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderHistoryAdapter);
    }
}
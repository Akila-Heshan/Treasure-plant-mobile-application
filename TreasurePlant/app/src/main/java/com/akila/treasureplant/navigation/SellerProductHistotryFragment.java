package com.akila.treasureplant.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akila.treasureplant.R;
import com.akila.treasureplant.adapter.ProductAdapter;
import com.akila.treasureplant.model.ProductItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SellerProductHistotryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_product_histotry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("seller", Context.MODE_PRIVATE);

        ArrayList<ProductItem> productItemArrayList = new ArrayList<>();
        ProductAdapter productAdapter = new ProductAdapter(productItemArrayList, getActivity());

        firestore.collection("product")
                .whereEqualTo("seller_id", Integer.parseInt(sharedPreferences.getString("seller_id","")))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.i("logTP","Test2");
                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();

                        for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                            Log.i("logTP",documentSnapshot.get("title").toString());

                            productItemArrayList.add(new ProductItem(documentSnapshot.get("id").toString(),
                                    documentSnapshot.get("title").toString(),
                                    documentSnapshot.get("price").toString(),
                                    documentSnapshot.get("sold").toString(),
                                    Integer.parseInt(documentSnapshot.get("review").toString())));
                        }

                        productAdapter.notifyDataSetChanged();
                    }
                });

        RecyclerView recyclerView = view.findViewById(R.id.product_history_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(productAdapter);
    }
}
package com.akila.treasureplant.navigation;

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
import android.widget.EditText;

import com.akila.treasureplant.R;
import com.akila.treasureplant.adapter.ProductAdapter;
import com.akila.treasureplant.model.ProductItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.search_recyclerView1);

        ArrayList<ProductItem> productItemArrayList = new ArrayList<ProductItem>();
        ProductAdapter productAdapter = new ProductAdapter(productItemArrayList, getActivity());

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("product")
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

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(productAdapter);

        view.findViewById(R.id.searchButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText searchText = view.findViewById(R.id.search_editText);

                productItemArrayList.clear();

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("product")
                        .where(Filter.greaterThanOrEqualTo("title", String.valueOf(searchText.getText())))
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

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(productAdapter);
            }
        });

    }
}
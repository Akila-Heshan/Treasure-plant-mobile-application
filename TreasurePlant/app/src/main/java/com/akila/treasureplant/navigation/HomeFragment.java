package com.akila.treasureplant.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = view.findViewById(R.id.goToSearchbutton);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels - 500;

        Log.i("logTP",String.valueOf(height));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHome1);
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();

        params.height = height;
        recyclerView.setLayoutParams(params);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("logTP","Click");
                fragmentManager.beginTransaction().replace(R.id.frame_layout_1, SearchFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

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



    }
}
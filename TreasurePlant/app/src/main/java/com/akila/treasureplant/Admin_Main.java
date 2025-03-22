package com.akila.treasureplant;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akila.treasureplant.adapter.SellerAdapter;
import com.akila.treasureplant.model.Seller;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Admin_Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        ArrayList<Seller> sellers = new ArrayList<>();
        SellerAdapter sellerAdapter = new SellerAdapter(sellers, this);

        firestore.collection("seller").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {

                            HashMap hashMap = (HashMap) documentSnapshot.get("location");

                            firestore.collection("user")
                                            .where(Filter.equalTo("id", Integer.parseInt(String.valueOf(documentSnapshot.get("user_id")))))
                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            DocumentSnapshot documentSnapshot1 = task.getResult().getDocuments().get(0);
                                            Log.d("logTP", "onComplete: " + documentSnapshot1.get("fname") + " " + documentSnapshot1.get("lname"));

                                            sellers.add(new Seller(
                                                    documentSnapshot.getId(),
                                                    documentSnapshot1.get("fname") + " " + documentSnapshot1.get("lname"),
                                                    documentSnapshot.getBoolean("isBlock"),
                                                    documentSnapshot.getString("mobile"),
                                                    String.valueOf(hashMap.get("latitude")),
                                                    String.valueOf(hashMap.get("longitude"))
                                            ));
                                            sellerAdapter.notifyDataSetChanged();
                                        }
                                    });


                        }
                        sellerAdapter.notifyDataSetChanged();
                    }
                });

        RecyclerView recyclerView = findViewById(R.id.adminRecyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sellerAdapter);

        BarChart barChart = findViewById(R.id.admin_barchart);
        List<BarEntry> barEntries = new ArrayList<>();

        firestore.collection("chart")
                .orderBy("value")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                        int x = 0;
                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            barEntries.add(new BarEntry(x, Integer.parseInt(String.valueOf(documentSnapshot.get("value")))));
                            x = x+2;
                        }

                        int empty = 30 - documentSnapshotList.size();

                        for (int i = 0; i < empty; i++) {
                            barEntries.add(new BarEntry(x, 0));
                            x = x+2;
                        }

                        BarDataSet barDataSet = new BarDataSet(barEntries,"Daily Income");

                        barDataSet.setColor(getColor(R.color.color6));

                        BarData barData = new BarData();
                        barData.addDataSet(barDataSet);

                        //customize the bars
                        barData.setBarWidth(1);

                        //customize the chart
                        barChart.setPinchZoom(false);
                        barChart.setScaleXEnabled(false);
                        barChart.animateY(2000, Easing.EaseInCubic);
                        barChart.setDescription(null);
                        barChart.setFitBars(true);

                        barChart.setData(barData);




//        barChart.getLegend().setCustom(legendEntryArrayList);
//        barChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                        barChart.getLegend().setXEntrySpace(48);

                        barChart.invalidate();
                    }
                });




    }
}
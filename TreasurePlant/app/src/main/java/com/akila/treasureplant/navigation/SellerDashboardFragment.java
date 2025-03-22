package com.akila.treasureplant.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akila.treasureplant.R;
import com.akila.treasureplant.SignInActivity;
import com.akila.treasureplant.Treasure_Plant_Seller;
import com.akila.treasureplant.model.DataClearer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SellerDashboardFragment extends Fragment {

    FirebaseFirestore db;
    String sellerId;

    BarChart dailyProfitChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        dailyProfitChart = view.findViewById(R.id.barChart1);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("seller", Context.MODE_PRIVATE);

        sellerId = sharedPreferences.getString("seller_id", null);

        loadDailyIncomeData();

        view.findViewById(R.id.seller_logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("seller", Context.MODE_PRIVATE);
                sharedPreferences.edit().remove("seller_id").apply();
//                DataClearer.clearAppData(getContext());
                startActivity(new Intent(getActivity(), Treasure_Plant_Seller.class));
            }
        });
    }

    private void loadDailyIncomeData() {
        db.collection("order")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Float> dailyIncome = new HashMap<>();

                        for (QueryDocumentSnapshot orderDocument : task.getResult()) {
                            ArrayList<HashMap<String, Object>> orderArray = (ArrayList<HashMap<String, Object>>) orderDocument.get("order_array");
                            Log.i("logTP",String.valueOf(orderArray.size()));
                            if (orderArray != null) {
                                for (HashMap<String, Object> orderItem : orderArray) {
                                    String productId = (String) orderItem.get("id");
                                    if (productId != null) {
                                        db.collection("product")
                                                .whereEqualTo("id", Integer.parseInt(productId))
                                                .get()
                                                .addOnCompleteListener(productTask -> {
                                                    if (productTask.isSuccessful() && !productTask.getResult().isEmpty()) { // Check if result is not empty
                                                        String productSellerId = String.valueOf(productTask.getResult().getDocuments().get(0).get("seller_id"));

                                                        if (productSellerId != null && productSellerId.equals(this.sellerId)) {
                                                            String orderDateStr = String.valueOf(orderDocument.get("date"));
                                                            String orderPriceStr = String.valueOf(orderItem.get("price"));

                                                            if (orderPriceStr != null) {
                                                                try {
                                                                    float orderPrice = Float.parseFloat(orderPriceStr);
                                                                    float income = orderPrice;

                                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                                                    Date orderDate = dateFormat.parse(orderDateStr);
                                                                    String formattedDate = dateFormat.format(orderDate);

                                                                    dailyIncome.put(formattedDate, dailyIncome.getOrDefault(formattedDate, 0f) + income);

                                                                    // Check if it's the last item in the last document
                                                                    if (orderDocument.getId().equals(task.getResult().getDocuments().get(task.getResult().size() - 1).getId()) &&
                                                                            orderArray.indexOf(orderItem) == orderArray.size() - 1) {
                                                                        createAndShowChart(dailyIncome);
                                                                    }

                                                                } catch (ParseException | NumberFormatException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        } else {
                                                            Log.d("logTP", "Product seller ID: " + productSellerId + ", Local seller ID: " + this.sellerId);
                                                        }
                                                    } else {
                                                        Log.e("logTP", "Error fetching product: " + productId, productTask.getException());
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e("logTP", "Error getting orders", task.getException());
                    }
                });
    }


    private void createAndShowChart(Map<String, Float> dailyIncome) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Sort dailyIncome entries by date
        List<Map.Entry<String, Float>> sortedEntries = new ArrayList<>(dailyIncome.entrySet());
        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                try {
                    Date date1 = inputDateFormat.parse(o1.getKey());
                    Date date2 = inputDateFormat.parse(o2.getKey());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        int xIndex = 0;
        for (Map.Entry<String, Float> entry : sortedEntries) {
            try {
                Date date = inputDateFormat.parse(entry.getKey());
                float income = entry.getValue();

                entries.add(new BarEntry(xIndex, income));
                labels.add(displayDateFormat.format(date));

                xIndex++;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Daily Income");
        dataSet.setColor(getResources().getColor(R.color.color6));

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData barData = new BarData(dataSets);
        dailyProfitChart.setData(barData);

        dailyProfitChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        dailyProfitChart.getXAxis().setGranularity(1f);
        dailyProfitChart.getXAxis().setLabelCount(labels.size());
        dailyProfitChart.getXAxis().setDrawGridLines(false);

        dailyProfitChart.invalidate();
    }
}
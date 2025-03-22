package com.akila.treasureplant.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.akila.treasureplant.R;
import com.akila.treasureplant.model.OrderHistoryItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {

    ArrayList<OrderHistoryItem> orderHistoryItems;
    FragmentActivity activity;

    boolean isVoted = false;

    public OrderHistoryAdapter(ArrayList<OrderHistoryItem> orderHistoryItems, FragmentActivity activity) {
        this.orderHistoryItems = orderHistoryItems;
        this.activity = activity;
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_history_item, parent, false);
        return new OrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {
        OrderHistoryItem orderHistoryItem = orderHistoryItems.get(position);
        holder.titleTextView.setText(orderHistoryItem.getTitle());
        holder.priceTextView.setText(orderHistoryItem.getPrice());
        holder.quantityTextView.setText(orderHistoryItem.getQuantity());

        if (orderHistoryItem.getReview() != 0) {
            isVoted = true;
        }

        if(orderHistoryItem.getReview() == 1){
            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.star);
            holder.star1.setCompoundDrawables(drawable,null,null,null);
            holder.star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));
        } else if (orderHistoryItem.getReview() == 2) {
            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.star);
            holder.star1.setCompoundDrawables(drawable,null,null,null);
            holder.star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star2.setCompoundDrawables(drawable,null,null,null);
            holder.star2.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));
        } else if (orderHistoryItem.getReview() == 3) {
            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.star);
            holder.star1.setCompoundDrawables(drawable,null,null,null);
            holder.star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star2.setCompoundDrawables(drawable,null,null,null);
            holder.star2.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star3.setCompoundDrawables(drawable,null,null,null);
            holder.star3.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));
        } else if (orderHistoryItem.getReview() == 4) {
            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.star);
            holder.star1.setCompoundDrawables(drawable,null,null,null);
            holder.star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star2.setCompoundDrawables(drawable,null,null,null);
            holder.star2.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star3.setCompoundDrawables(drawable,null,null,null);
            holder.star3.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star4.setCompoundDrawables(drawable,null,null,null);
            holder.star4.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));
        } else if (orderHistoryItem.getReview() == 5) {
            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.star);
            holder.star1.setCompoundDrawables(drawable,null,null,null);
            holder.star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star2.setCompoundDrawables(drawable,null,null,null);
            holder.star2.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star3.setCompoundDrawables(drawable,null,null,null);
            holder.star3.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star4.setCompoundDrawables(drawable,null,null,null);
            holder.star4.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star5.setCompoundDrawables(drawable,null,null,null);
            holder.star5.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));
        }

        holder.star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewUpdate(1, orderHistoryItem.getId());
                Log.i("logTP", "1");
            }
        });

        holder.star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewUpdate(2, orderHistoryItem.getId());
                Log.i("logTP", "1");
            }
        });

        holder.star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewUpdate(3, orderHistoryItem.getId());
            }
        });

        holder.star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewUpdate(4, orderHistoryItem.getId());
            }
        });

        holder.star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewUpdate(5, orderHistoryItem.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderHistoryItems.size();
    }

    private void reviewUpdate(int review, String id) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Log.i("logTP", "reviewUpdate");
        if (!isVoted) {

            Log.i("logTP", "reviewUpdate true");
            firestore.collection("product").document(id)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            int rev = Integer.parseInt(String.valueOf(documentSnapshot.get("review")));
                            int rev_count = Integer.parseInt(String.valueOf(documentSnapshot.get("review_count")));

                            firestore.collection("product").document(id)
                                    .update("review", rev+review, "review_count", rev_count+1);


                            SharedPreferences sharedPreferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
                            String user_id = sharedPreferences.getString("id",null);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("product_id", id);
                            hashMap.put("user_id", user_id);
                            hashMap.put("review", String.valueOf(review));

                            firestore.collection("review").add(hashMap);
                        }
                    });

            Toast.makeText(activity, "Thank you for your review", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(activity, "You have already given your review", Toast.LENGTH_SHORT).show();

    }
    static class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView priceTextView;
        TextView quantityTextView;
        TextView star1;
        TextView star2;
        TextView star3;
        TextView star4;
        TextView star5;

        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.order_history_item_title);
            priceTextView = itemView.findViewById(R.id.order_history_item_price);
            quantityTextView = itemView.findViewById(R.id.order_history_item_quantity);
            star1 = itemView.findViewById(R.id.order_history_item_star1);
            star2 = itemView.findViewById(R.id.order_history_item_star2);
            star3 = itemView.findViewById(R.id.order_history_item_star3);
            star4 = itemView.findViewById(R.id.order_history_item_star4);
            star5 = itemView.findViewById(R.id.order_history_item_star5);
        }
    }
}

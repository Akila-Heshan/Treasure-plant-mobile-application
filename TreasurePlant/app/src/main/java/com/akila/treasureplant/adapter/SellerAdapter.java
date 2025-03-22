package com.akila.treasureplant.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.akila.treasureplant.Admin_Main;
import com.akila.treasureplant.LocationActivity;
import com.akila.treasureplant.R;
import com.akila.treasureplant.model.Seller;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.SellerViewHolder> {

    ArrayList<Seller> sellers;
    Activity activity;

    public SellerAdapter(ArrayList<Seller> sellers, Activity activity) {
        this.sellers = sellers;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.seller_item, parent, false);
        SellerViewHolder sellerViewHolder = new SellerViewHolder(view);
        return sellerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerViewHolder holder, int position) {
        holder.name.setText(sellers.get(position).getName());

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + sellers.get(position).getMobile()));

                    if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        v.getContext().startActivity(intent);
                    } else {
                        ActivityCompat.requestPermissions((AppCompatActivity) v.getContext(), new String[]{Manifest.permission.CALL_PHONE}, 1); // Replace 1 with a request code
                    }

                } catch (SecurityException e) {
                    Toast.makeText(v.getContext(), "Call permission is required.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(v.getContext(), "Could not make the call.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        if (sellers.get(position).isBlock()) {
            holder.isBlock.setText("Unblock");
            holder.isBlock.setBackgroundColor(activity.getColor(R.color.color6));
        } else {
            holder.isBlock.setText("Block");
            holder.isBlock.setBackgroundColor(activity.getColor(R.color.color5));
        }

        holder.isBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("seller").document(String.valueOf(sellers.get(position).getId())).update("isBlock", !sellers.get(position).isBlock());
                activity.startActivity(new Intent(activity, Admin_Main.class));
                activity.finish();
            }
        });

        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, LocationActivity.class);
                intent.putExtra("latitude", sellers.get(position).getLatitude());
                intent.putExtra("longitude", sellers.get(position).getLongitude());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sellers.size();
    }

    static class SellerViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView call;
        Button isBlock;
        Button location;

        public SellerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.seller_item_name);
            call = itemView.findViewById(R.id.seller_item_call);
            isBlock = itemView.findViewById(R.id.seller_item_isBlock_button);
            location = itemView.findViewById(R.id.seller_item_location_button);
        }
    }
}

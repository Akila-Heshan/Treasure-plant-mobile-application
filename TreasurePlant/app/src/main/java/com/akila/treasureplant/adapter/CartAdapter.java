package com.akila.treasureplant.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akila.treasureplant.R;
import com.akila.treasureplant.model.CartItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    ArrayList<CartItem> cartItemArrayList;
    Activity activity;
    public CartAdapter(ArrayList<CartItem> cartItemArrayList, Activity activity) {
        this.cartItemArrayList = cartItemArrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflatedView = layoutInflater.inflate(R.layout.cart_item, parent, false);
        CartViewHolder cartViewHolder = new CartViewHolder(inflatedView);
        return cartViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemArrayList.get(position);
        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+cartItem.getId()+"/image0.png").into(holder.imageView);
        holder.title.setText(cartItem.getTitle());
        holder.price.setText(cartItem.getPrice());
        holder.quantity.setText("Quantity : "+cartItem.getQuantity());

        String totalPrice = String.valueOf(Integer.parseInt(cartItem.getPrice()) * Integer.parseInt(cartItem.getQuantity()));
        holder.priceTotal.setText(totalPrice);

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("logTP", cartItem.getId()+" : Remove Clicked");
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemArrayList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title;
        TextView price;
        TextView quantity;
        TextView priceTotal;
        TextView remove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cartItemImage);
            title = itemView.findViewById(R.id.cartItemTitle);
            price = itemView.findViewById(R.id.cartItemPrice);
            quantity = itemView.findViewById(R.id.cartItemQuantity);
            priceTotal = itemView.findViewById(R.id.cartItemPriceTotal);
            remove = itemView.findViewById(R.id.cartItemRemove);
        }
    }

}

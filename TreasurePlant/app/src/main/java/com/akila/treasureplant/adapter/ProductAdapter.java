package com.akila.treasureplant.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.akila.treasureplant.R;
import com.akila.treasureplant.model.ProductItem;
import com.akila.treasureplant.navigation.SearchFragment;
import com.akila.treasureplant.navigation.SingelProductFragment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    ArrayList<ProductItem> productItemArrayList;
    FragmentActivity activity;

    public ProductAdapter(ArrayList<ProductItem> productItemArrayList, FragmentActivity activity){
        this.productItemArrayList = productItemArrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflatedView = layoutInflater.inflate(R.layout.product_item, parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(inflatedView);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductItem productItem = productItemArrayList.get(position);

        holder.title.setText(productItem.getTitle());
        holder.price.setText("Rs. "+productItem.getPrice());
        holder.sold.setText(productItem.getSold()+" Sold");
        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+productItem.getId()+"/image0.png").into(holder.imageView);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("logTP",productItem.getId());

                Bundle args = new Bundle();

                SingelProductFragment.id = productItem.getId();

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_1, SingelProductFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        if(productItem.getReview() == 1){
            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.star);
            holder.star1.setCompoundDrawables(drawable,null,null,null);
            holder.star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));
        } else if (productItem.getReview() == 2) {
            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.star);
            holder.star1.setCompoundDrawables(drawable,null,null,null);
            holder.star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));

            holder.star2.setCompoundDrawables(drawable,null,null,null);
            holder.star2.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            drawable.setTint(activity.getColor(R.color.color5));
        } else if (productItem.getReview() == 3) {
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
        } else if (productItem.getReview() == 4) {
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
        } else if (productItem.getReview() == 5) {
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
    }

    @Override
    public int getItemCount() {
        return productItemArrayList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView price;
        TextView sold;
        TextView star1;
        TextView star2;
        TextView star3;
        TextView star4;
        TextView star5;
        ConstraintLayout constraintLayout;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productItemImage);
            title = itemView.findViewById(R.id.productItemTitle);
            price = itemView.findViewById(R.id.productItemPrice);
            sold = itemView.findViewById(R.id.productItemSold);
            star1 = itemView.findViewById(R.id.productItemStar1);
            star2 = itemView.findViewById(R.id.productItemStar2);
            star3 = itemView.findViewById(R.id.productItemStar3);
            star4 = itemView.findViewById(R.id.productItemStar4);
            star5 = itemView.findViewById(R.id.productItemStar5);
            constraintLayout = itemView.findViewById(R.id.productItemConstraintLayout);
        }
    }

}

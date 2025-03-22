package com.akila.treasureplant.navigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akila.treasureplant.R;
import com.akila.treasureplant.adapter.ProductAdapter;
import com.akila.treasureplant.model.CartProduct;
import com.akila.treasureplant.model.ProductItem;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class SingelProductFragment extends Fragment {

    public static String id;

    private final ActivityResultLauncher<Intent> payHereLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                        Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if (serializable instanceof PHResponse) {
                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;
                            String msg = response.isSuccess() ? "Payment Success: "+ response.getData() : "Payment failed: " +response;
                            Log.i("logTP", msg);
                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    Log.i("logTP", "Payment canceled");
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_singel_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels - 700;

        Log.i("logTP",String.valueOf(height));

        RecyclerView recyclerView = view.findViewById(R.id.singleProductRecyclerView);
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();

        params.height = height;
        recyclerView.setLayoutParams(params);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        final int[] category_id = {0};

        final String[] value = {"",""};

        firestore.collection("product")
                .where(Filter.equalTo("id",Integer.parseInt(id)))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.i("logTP","onComplete Single Product");
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                            TextView title = view.findViewById(R.id.singleProductTitle);
                            TextView price = view.findViewById(R.id.singleProductPrice);
                            TextView review = view.findViewById(R.id.singleProductRating);
                            TextView ratingCount = view.findViewById(R.id.singleProductRatingCount);
                            TextView sold = view.findViewById(R.id.singleProductSold);
                            TextView description = view.findViewById(R.id.singleProductDescription);
                            TextView imageCount = view.findViewById(R.id.singleProductImageCount);
                            ImageView image = view.findViewById(R.id.singleProductImageView);

                            TextView star1 = view.findViewById(R.id.singleProductStar1);
                            TextView star2 = view.findViewById(R.id.singleProductStar2);
                            TextView star3 = view.findViewById(R.id.singleProductStar3);
                            TextView star4 = view.findViewById(R.id.singleProductStar4);
                            TextView star5 = view.findViewById(R.id.singleProductStar5);

                            Activity activity = getActivity();

                            category_id[0] = Integer.parseInt(String.valueOf(documentSnapshot.get("category_id")));

                            value[0] = String.valueOf(documentSnapshot.get("price"));
                            value[1] = String.valueOf(documentSnapshot.get("title"));

                            title.setText(String.valueOf(documentSnapshot.get("title")));
                            price.setText("Rs."+String.valueOf(documentSnapshot.get("price")));
                            ratingCount.setText(String.valueOf(documentSnapshot.get("review_count")));
                            sold.setText(String.valueOf(documentSnapshot.get("sold")));
                            description.setText(String.valueOf(documentSnapshot.get("description")));
                            imageCount.setText(String.valueOf(documentSnapshot.get("image")));

                            int imageCountValue = Integer.parseInt(String.valueOf(documentSnapshot.get("image"))) -1;

                            Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image0.png").into(image);

                            final int[] currentImage = {0};


                            view.findViewById(R.id.singleProductNextButton).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (currentImage[0] == 0) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image1.png").into(image);
                                        currentImage[0] = 1;
                                    } else if (currentImage[0] == 1 && currentImage[0] <= imageCountValue) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image1.png").into(image);
                                        currentImage[0] = 2;
                                    } else if (currentImage[0] == 2  && currentImage[0] <= imageCountValue) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image2.png").into(image);
                                        currentImage[0] = 3;
                                    } else if (currentImage[0] == 3  && currentImage[0] <= imageCountValue) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image3.png").into(image);
                                        currentImage[0] = 4;
                                    } else if (currentImage[0] == 4  && currentImage[0] <= imageCountValue) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image4.png").into(image);
                                        currentImage[0] = 5;
                                    } else if (currentImage[0] == 5  && currentImage[0] <= imageCountValue) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image0.png").into(image);
                                        currentImage[0] = 0;
                                    }
                                }
                            });

//                            view.findViewById(R.id.singleProductBackButton).setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    if (currentImage[0] == 0) {
//                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image4.png").into(image);
//                                        currentImage[0] = 4;
//                                    } else if (currentImage[0] == 4 && currentImage[0] <= imageCountValue) {
//                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image1.png").into(image);
//                                        currentImage[0] = 2;
//                                    } else if (currentImage[0] == 2  && currentImage[0] <= imageCountValue) {
//                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image2.png").into(image);
//                                        currentImage[0] = 3;
//                                    } else if (currentImage[0] == 3  && currentImage[0] <= imageCountValue) {
//                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image3.png").into(image);
//                                        currentImage[0] = 4;
//                                    } else if (currentImage[0] == 4  && currentImage[0] <= imageCountValue) {
//                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image4.png").into(image);
//                                        currentImage[0] = 5;
//                                    } else if (currentImage[0] == 5  && currentImage[0] <= imageCountValue) {
//                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image0.png").into(image);
//                                        currentImage[0] = 0;
//                                    }
//                                }
//                            });

                            view.findViewById(R.id.singleProductNextButton).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (currentImage[0] == 0) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image1.png").into(image);
                                        currentImage[0] = 1;
                                    } else if (currentImage[0] == 1) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image1.png").into(image);
                                        currentImage[0] = 2;
                                    } else if (currentImage[0] == 2) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image2.png").into(image);
                                        currentImage[0] = 3;
                                    } else if (currentImage[0] == 3) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image3.png").into(image);
                                        currentImage[0] = 4;
                                    } else if (currentImage[0] == 4) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image4.png").into(image);
                                        currentImage[0] = 5;
                                    } else if (currentImage[0] == 5) {
                                        Glide.with(activity).load(activity.getString(R.string.url)+"/images/"+id+"/image0.png").into(image);
                                        currentImage[0] = 0;
                                    }
                                }
                            });

                            int total_review = Integer.parseInt(String.valueOf(documentSnapshot.get("review")));
                            int review_count = Integer.parseInt(String.valueOf(documentSnapshot.get("review_count")));
                            double rating = Double.parseDouble(String.format("%.1f", (double)total_review/review_count));
                            int star_rating = (int)Math.round(rating);
//                            int star_rating = 1;

                            review.setText(String.valueOf(rating));

                            Log.i("logTP",String.valueOf(star_rating));


                            if(star_rating == 1){
                                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.star_new);
                                star1.setCompoundDrawables(drawable,null,null,null);
                                star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));
                            } else if (star_rating == 2) {
                                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.star_new);
                                star1.setCompoundDrawables(drawable,null,null,null);
                                star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));

                                star2.setCompoundDrawables(drawable,null,null,null);
                                star2.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));
                            } else if (star_rating == 3) {
                                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.star_new);
                                star1.setCompoundDrawables(drawable,null,null,null);
                                star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));

                                star2.setCompoundDrawables(drawable,null,null,null);
                                star2.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));

                                star3.setCompoundDrawables(drawable,null,null,null);
                                star3.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));
                            } else if (star_rating == 4) {
                                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.star_new);
                                star1.setCompoundDrawables(drawable,null,null,null);
                                star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));

                                star2.setCompoundDrawables(drawable,null,null,null);
                                star2.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));

                                star3.setCompoundDrawables(drawable,null,null,null);
                                star3.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));

                                star4.setCompoundDrawables(drawable,null,null,null);
                                star4.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));
                            } else if (star_rating == 5) {
                                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.star_new);
                                star1.setCompoundDrawables(drawable,null,null,null);
                                star1.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));

                                star2.setCompoundDrawables(drawable,null,null,null);
                                star2.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));

                                star3.setCompoundDrawables(drawable,null,null,null);
                                star3.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));

                                star4.setCompoundDrawables(drawable,null,null,null);
                                star4.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));

                                star5.setCompoundDrawables(drawable,null,null,null);
                                star5.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                                drawable.setTint(activity.getColor(R.color.color5));
                            }

                        }
                    }
                });

        ArrayList<ProductItem> productItemArrayList = new ArrayList<ProductItem>();
        ProductAdapter productAdapter = new ProductAdapter(productItemArrayList, getActivity());

        firestore.collection("product")
                .where(Filter.equalTo("category_id", category_id[0]))
                .limit(6)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.i("logTP","single product load complete");
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

        view.findViewById(R.id.singleProductBuyNowButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitRequest req = new InitRequest();
                req.setMerchantId("1229511");
                req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
                req.setAmount(1000.00);             // Final Amount to be charged
                req.setOrderId("1234567892");        // Unique Reference ID
                req.setItemsDescription("Door bell wireless");  // Item description title
                req.setCustom1("This is the custom message 1");
                req.setCustom2("This is the custom message 2");
                req.getCustomer().setFirstName("Akila");
                req.getCustomer().setLastName("Peera");
                req.getCustomer().setEmail("akila@gmail.com");
                req.getCustomer().setPhone("+94771239667");
                req.getCustomer().getAddress().setAddress("No.1,  Road");
                req.getCustomer().getAddress().setCity("Kandy");
                req.getCustomer().getAddress().setCountry("Sri Lanka");

                req.getCustomer().getDeliveryAddress().setAddress("No.6, Kandy Road");
                req.getCustomer().getDeliveryAddress().setCity("Mawanella");
                req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
                req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));
                req.setNotifyUrl("https://sample.com/test");

                Intent intent = new Intent(getActivity().getApplicationContext(), PHMainActivity.class);
                intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
                PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
                payHereLauncher.launch(intent);
            }
        });

        view.findViewById(R.id.singleProductAddToCartButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View alert = layoutInflater.inflate(R.layout.add_to_cart_alert, null, false);

                TextView unitPrice = alert.findViewById(R.id.add_to_cart_unit_price);
                TextView quantity =  alert.findViewById(R.id.add_to_cart_quantity);
                TextView totalPrice =  alert.findViewById(R.id.add_to_cart_total_price);

                int price = Integer.parseInt(value[0]);

                unitPrice.setText(value[0]);
                totalPrice.setText(value[0]);

                alert.findViewById(R.id.add_to_cart_alert_plus_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantityValue = Integer.parseInt(String.valueOf(quantity.getText()));
                        int newQuantity = ++quantityValue;
                        quantity.setText(String.valueOf(newQuantity));
                        totalPrice.setText(String.valueOf(newQuantity * price));
                    }
                });

                alert.findViewById(R.id.add_to_cart_alert_minus_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantityValue = Integer.parseInt(String.valueOf(quantity.getText()));
                        if (quantityValue != 1) {
                            int newQuantity = --quantityValue;
                            quantity.setText(String.valueOf(newQuantity));
                            totalPrice.setText(String.valueOf(newQuantity * price));
                        }
                    }
                });

                alert.findViewById(R.id.add_to_cart_add_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                        String userId = sharedPreferences.getString("id",null);
                        Log.i("logTP",userId);

                        firestore.collection("cart")
                                .where(Filter.equalTo("user_id", Integer.parseInt(userId)))
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                                        if(documentSnapshotList.isEmpty()){
                                            int cart_id = new Random().nextInt(100000000);

                                            ArrayList<CartProduct> cartProductArrayList = new ArrayList<>();
                                            cartProductArrayList.add(new CartProduct(
                                                    String.valueOf(id),
                                                    value[1],
                                                    value[0],
                                                    String.valueOf(quantity.getText())
                                            ));

                                            HashMap<String, Object> cartMap = new HashMap<>();
                                            cartMap.put("id",cart_id);
                                            cartMap.put("user_id", Integer.parseInt(userId));
                                            cartMap.put("product_array", cartProductArrayList);

                                            firestore.collection("cart").add(cartMap)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(getActivity().getApplicationContext(), "Item Added Successfully", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity().getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                        }else{
                                            DocumentSnapshot snapshot = documentSnapshotList.get(0);
                                            List<HashMap> cartMaps = (List<HashMap>) snapshot.get("product_array");
                                            Log.i("logTP",String.valueOf(cartMaps));

                                            boolean productFound = false;

                                            for (HashMap<String, Object> map : cartMaps) {

                                                if (String.valueOf(map.get("id")).equals(id)) {
                                                    productFound = true;

                                                    firestore.collection("product")
                                                            .where(Filter.equalTo("id", Integer.parseInt(id)))
                                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                                                                    int qty = Integer.parseInt(String.valueOf(documentSnapshot.get("qty")));
                                                                    int updateQuantity = Integer.parseInt(String.valueOf(quantity.getText())) + Integer.parseInt(String.valueOf(map.get("quantity")));

                                                                    if (qty >= updateQuantity) {
                                                                        Log.i("logTP", "Id : " + snapshot.getId());

                                                                        map.put("quantity", String.valueOf(updateQuantity));
                                                                        Log.i("logTP",String.valueOf(cartMaps));

                                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                                        hashMap.put("product_array", cartMaps);

                                                                        firestore.collection("cart").document(snapshot.getId())
                                                                                        .update(hashMap)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                                Toast.makeText(getActivity().getApplicationContext(), "Quantity Updated", Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                Toast.makeText(getActivity().getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        });


                                                                    }else {
                                                                        Toast.makeText(getActivity().getApplicationContext(), "Unavailable Quantity", Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });

                                                    break;
                                                }
                                            }

                                            if (!productFound) {

                                                firestore.collection("product")
                                                        .where(Filter.equalTo("id", Integer.parseInt(id)))
                                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                                                                int qty = Integer.parseInt(String.valueOf(quantity.getText()));
                                                                int availableQty = Integer.parseInt(String.valueOf(documentSnapshot.get("qty")));

                                                                if (availableQty >= qty) {
                                                                    HashMap<String,Object> hashMap = new HashMap<>();
                                                                    hashMap.put("id", id);
                                                                    hashMap.put("price", value[0]);
                                                                    hashMap.put("title", value[1]);
                                                                    hashMap.put("quantity", String.valueOf(qty));

                                                                    cartMaps.add(hashMap);

                                                                    HashMap<String, Object> document = new HashMap<>();
                                                                    document.put("product_array", cartMaps);

                                                                    firestore.collection("cart").document(snapshot.getId())
                                                                            .update(document)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Toast.makeText(getActivity().getApplicationContext(), "Product Added Successfully", Toast.LENGTH_LONG).show();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Toast.makeText(getActivity().getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();
                                                                                }
                                                                            });

                                                                } else
                                                                    Toast.makeText(getActivity().getApplicationContext(), "Unavailable Quantity", Toast.LENGTH_LONG).show();

                                                            }
                                                        });

                                            }

                                        }
                                    }
                                });
                    }
                });

                new AlertDialog.Builder(getActivity()).setView(alert).show();
            }
        });

        view.findViewById(R.id.singleProductWishList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                String userId = sharedPreferences.getString("id",null);
                Log.i("logTP", userId );
            }
        });

    }
}
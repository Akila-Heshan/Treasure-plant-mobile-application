package com.akila.treasureplant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Treasure_Plant_Seller extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_treasure_plant_seller);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("seller", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("seller_id",null);

        if (id != null){
            startActivity(new Intent(Treasure_Plant_Seller.this, SellerMainActivity.class));
        }

        findViewById(R.id.sellerLoginButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.sellerLoginEditTextEmail);
                EditText password = findViewById(R.id.sellerLoginEditTextPassword);



                firestore.collection("user")
                        .where(Filter.and(
                                Filter.equalTo("email", String.valueOf(email.getText())),
                                Filter.equalTo("password", String.valueOf(password.getText())),
                                Filter.equalTo("isGoogle", false)
                        ))
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                                Log.i("logTP", String.valueOf(documentSnapshotList.size()));
                                if (documentSnapshotList.size() == 1) {

                                    firestore.collection("seller")
                                            .where(Filter.equalTo("user_id", Integer.parseInt(String.valueOf(documentSnapshotList.get(0).get("id")))))
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    List<DocumentSnapshot> documentSnapshotList1 = task.getResult().getDocuments();

                                                    if (documentSnapshotList1.isEmpty()) {
                                                        TextInputLayout textInputLayout = findViewById(R.id.sellerSignInEmailTextInputLayout);
                                                        TextInputLayout textInputLayout2 = findViewById(R.id.sellerSigninPasswordTextInputLayout);

                                                        textInputLayout.setErrorEnabled(true);
                                                        textInputLayout2.setErrorEnabled(true);

                                                        textInputLayout.setError("This account is a not seller account");
                                                        textInputLayout2.setError("");
                                                    } else {
                                                        boolean isBlock = Boolean.parseBoolean(String.valueOf(documentSnapshotList1.get(0).get("isBlock")));

                                                        if (isBlock) {
                                                            // seller blocked

                                                            LayoutInflater layoutInflater = LayoutInflater.from(Treasure_Plant_Seller.this);
                                                            View alert = layoutInflater.inflate(R.layout.seller_block_alert_layout, null, false);

                                                            TextInputLayout textInputLayout = findViewById(R.id.sellerSignInEmailTextInputLayout);
                                                            TextInputLayout textInputLayout2 = findViewById(R.id.sellerSigninPasswordTextInputLayout);

                                                            textInputLayout.setErrorEnabled(true);
                                                            textInputLayout2.setErrorEnabled(true);
                                                            textInputLayout.setError("");
                                                            textInputLayout2.setError("");


                                                            new AlertDialog.Builder(Treasure_Plant_Seller.this).setView(alert).show();

                                                        } else {
                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.putBoolean("isGoogle", false);
                                                            editor.putString("fname", String.valueOf(documentSnapshotList.get(0).get("fname")));
                                                            editor.putString("lname", String.valueOf(documentSnapshotList.get(0).get("lname")));
                                                            editor.putString("seller_id", String.valueOf(documentSnapshotList1.get(0).get("id")));
                                                            editor.apply();
                                                            startActivity(new Intent(Treasure_Plant_Seller.this, SellerMainActivity.class));
                                                        }
                                                    }
                                                }
                                            });
                                } else {
                                    Log.i("logTP","Login Failed");

                                    firestore.collection("user")
                                            .where(Filter.and(
                                                    Filter.equalTo("email", String.valueOf(email.getText())),
                                                    Filter.equalTo("isGoogle", false)
                                            )).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    List<DocumentSnapshot> documentSnapshotList1 = task.getResult().getDocuments();
                                                    if (documentSnapshotList1.size() == 1) {
                                                        //invalid Password
                                                        TextInputLayout textInputLayout = findViewById(R.id.sellerSigninPasswordTextInputLayout);
                                                        textInputLayout.setErrorEnabled(true);
                                                        textInputLayout.setError("Invalid Password");

                                                        TextInputLayout textInputLayout2 = findViewById(R.id.sellerSignInEmailTextInputLayout);
                                                        textInputLayout2.setErrorEnabled(false);
                                                    } else {
                                                        // invalid email
                                                        TextInputLayout textInputLayout = findViewById(R.id.sellerSignInEmailTextInputLayout);
                                                        textInputLayout.setErrorEnabled(true);
                                                        textInputLayout.setError("Invalid Email");

                                                        TextInputLayout textInputLayout2 = findViewById(R.id.sellerSigninPasswordTextInputLayout);
                                                        textInputLayout2.setErrorEnabled(false);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        });

        findViewById(R.id.sellerLoginGoogleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Treasure_Plant_Seller.this, SellerMainActivity.class));
            }
        });

        findViewById(R.id.sellerSigninToSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Treasure_Plant_Seller.this, SellerRegistation.class));
            }
        });



    }
}
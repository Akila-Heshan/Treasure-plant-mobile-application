package com.akila.treasureplant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.akila.treasureplant.model.NetworkUtils;

public class SellerSplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetworkUtils.isNetworkAvailable(SellerSplashScreenActivity.this)) {
                    startActivity(new Intent(SellerSplashScreenActivity.this, Treasure_Plant_Seller.class));
                    finish();
                } else {
                    ImageView imageView = findViewById(R.id.sellerSplashImageView);
                    imageView.setImageResource(R.drawable.no_internet);
                }
            }
        }, 3000);
    }
}
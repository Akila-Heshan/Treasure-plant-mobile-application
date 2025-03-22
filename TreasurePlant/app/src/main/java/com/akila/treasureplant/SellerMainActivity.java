package com.akila.treasureplant;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.akila.treasureplant.navigation.SellerDashboardFragment;
import com.akila.treasureplant.navigation.SellerProductAddFragment;
import com.akila.treasureplant.navigation.SellerProductHistotryFragment;
import com.google.android.material.navigation.NavigationView;

public class SellerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.seller_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.seller_frame_layout, SellerDashboardFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        DrawerLayout drawerLayout = findViewById(R.id.seller_main);
        FrameLayout frameLayout = findViewById(R.id.seller_frame_layout);
        NavigationView navigationView  = findViewById(R.id.seller_navigation_view);

        findViewById(R.id.sellerToggelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.seller_menu_item_dashboard) {
                    fragmentManager.beginTransaction().replace(R.id.seller_frame_layout, SellerDashboardFragment.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                } else if (item.getItemId() == R.id.seller_menu_item_add_product) {
                    fragmentManager.beginTransaction().replace(R.id.seller_frame_layout, SellerProductAddFragment.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                }  else if (item.getItemId() == R.id.seller_menu_item_product_history) {
                    fragmentManager.beginTransaction().replace(R.id.seller_frame_layout, SellerProductHistotryFragment.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });

    }
}
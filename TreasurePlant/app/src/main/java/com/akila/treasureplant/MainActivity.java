package com.akila.treasureplant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.akila.treasureplant.model.AirplaneModeChangeReceiver;
import com.akila.treasureplant.navigation.CartFragment;
import com.akila.treasureplant.navigation.HomeFragment;
import com.akila.treasureplant.navigation.SearchFragment;
import com.akila.treasureplant.navigation.UserProfileFragment;
import com.akila.treasureplant.navigation.WishListBlankFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static boolean isFlip;
    private AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DrawerLayout drawerLayout = findViewById(R.id.main);
//        Toolbar toolbar = findViewById(R.id.toolbar1);
        FrameLayout frameLayout = findViewById(R.id.frame_layout_1);
        NavigationView navigationView  = findViewById(R.id.navigation_view_1);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.frame_layout_1, HomeFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        findViewById(R.id.toggelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
//                ImageView imageView = findViewById(R.id.carouselImageView);
//                imageView.setImageResource(R.drawable.carousel_image2);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i("logTP",item.toString());

                if(item.getItemId() == R.id.menu_item_home){
                    fragmentManager.beginTransaction().replace(R.id.frame_layout_1, HomeFragment.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id",null);

        if (id != null){
            boolean isGoogle = sharedPreferences.getBoolean("isGoogle",false);
            TextView  textView = findViewById(R.id.userTextView);
            ImageView imageView = findViewById(R.id.userImageView);
            Log.i("logTP","user id : "+id);
            if (isGoogle) {
                String fname = sharedPreferences.getString("fname",null);
                textView.setText(fname);
                String image = sharedPreferences.getString("image",null);
                Glide.with(this).load(image).into(imageView);
            } else {
                String fname = sharedPreferences.getString("fname",null);
                String lname = sharedPreferences.getString("lname",null);
                textView.setText(fname+" "+lname);
            }

        }else{
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          ImageView imageView = findViewById(R.id.carouselImageView);
                          imageView.setImageDrawable(getDrawable(R.drawable.carousel_image2));
                      }
                  });
            }
        });

        findViewById(R.id.goToHomeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_1, HomeFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        findViewById(R.id.goToCartButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_1, CartFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        findViewById(R.id.goToWishListButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_1, WishListBlankFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        findViewById(R.id.goToProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout_1, UserProfileFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        int[] flipCount = {0};
        SensorManager sensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            Log.i("logTP","ACCELEROMETER Sensor found");
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            SensorEventListener sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float value[] = event.values;
                    float z = value[2];
//                            Log.i("App17Log",String.valueOf(z));
                    if(z <= -5 && !isFlip ){
                        ++flipCount[0];

                        if (flipCount[0] == 1) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    flipCount[0] = 0;
                                }
                            }, 3500);
                        }

                        if (flipCount[0] == 4) {
                            Log.d("logTP", "open report alert");
                            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                            View alert = layoutInflater.inflate(R.layout.report_alert_layout, null, false);

                            EditText editText = alert.findViewById(R.id.report_input1);
                            Button button = alert.findViewById(R.id.report_button1);

                            new AlertDialog.Builder(MainActivity.this).setView(alert).show();
                        }

                        Log.i("logTP","Flip");
                        isFlip=true;

                    }
                    if(z > 9 && isFlip){
                        Log.i("logTP","Normal");
                        isFlip=false;
                    }

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

            sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        } else
            Log.i("App17Log","ACCELEROMETER Sensor not found");

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneModeChangeReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneModeChangeReceiver);
    }
}
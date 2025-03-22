package com.akila.treasureplant.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import com.akila.treasureplant.SplashScreenActivity;

public class AirplaneModeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (isAirplaneModeOn(context.getApplicationContext())) {
            Toast.makeText(context, "Airplane mode is on", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, SplashScreenActivity.class).putExtra("isAirplaneModeOn",true));
        } else {
            Toast.makeText(context, "Airplane mode is off", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
}
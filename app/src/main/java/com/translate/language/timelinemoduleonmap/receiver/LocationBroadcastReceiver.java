package com.translate.language.timelinemoduleonmap.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.translate.language.timelinemoduleonmap.service.LocationService;

import java.util.Objects;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LocationBroadcastReceiver", "onReceive:  before ");
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("LocationBroadcastReceiver", "onReceive:  recieve it 1212 ");
            String toastText = "Location Reboot Grape";
            Toast.makeText(context.getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
            startService(context.getApplicationContext());
        } else {
            startService(context.getApplicationContext());

        }
    }

    private void startService(Context context) {
        Intent intentService = new Intent(context, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }


}

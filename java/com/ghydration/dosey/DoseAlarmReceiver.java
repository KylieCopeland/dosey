package com.ghydration.dosey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DoseAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Your code here. For example, you could start a service or show a notification.
        System.out.println("Alarm fired!");
    }
}

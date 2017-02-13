package com.blackrubystudio.aipel3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blackrubystudio.aipel3.sms.SmsService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, SmsService.class);
            context.startService(i);
        }
    }
}

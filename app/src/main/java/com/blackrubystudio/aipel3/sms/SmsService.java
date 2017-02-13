package com.blackrubystudio.aipel3.sms;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.blackrubystudio.aipel3.api.CategoryAPI;
import com.blackrubystudio.aipel3.api.gson.Category;
import com.blackrubystudio.aipel3.db.DatabaseHelper;
import com.blackrubystudio.aipel3.model.AccountingItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmsService extends Service {
    private SmsObject smsObject;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(broadcastReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        // Initialize database
        if(dbHelper == null){
            dbHelper = new DatabaseHelper(getApplicationContext());
        } else {
            dbHelper.drop(DatabaseHelper.Table.EXPENSE);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( intent == null) {
            registerReceiver(broadcastReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            smsObject = new SmsParser().parse(intent);
            CategoryAPI categoryAPI = new CategoryAPI();
            categoryAPI.getCategory(smsObject.getPlace()).enqueue(categoryCallback);
        }
    };


    private Callback<Category> categoryCallback = new Callback<Category>() {
        @Override
        public void onResponse(Call<Category> call, Response<Category> response) {
            try {
                AccountingItem accountingItem = new AccountingItem(response.body().getPlace(),
                        response.body().getIndex(),
                        Integer.parseInt(smsObject.getPrice()),
                        Integer.parseInt(smsObject.getDate()),
                        Integer.parseInt(smsObject.getTime()));

                AddItem(accountingItem);

                Intent intent = new Intent("SMS_RECEIVED");
                sendBroadcast(intent);
            } catch (NumberFormatException e) {
                e.printStackTrace();

            }
        }

        @Override
        public void onFailure(Call<Category> call, Throwable t) {

        }
    };

    private void AddItem(AccountingItem accountingItem){
        if(dbHelper == null){
            dbHelper = new DatabaseHelper(getApplicationContext());
        }
        dbHelper.insert(DatabaseHelper.Table.EXPENSE, accountingItem);
    }

    public static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SmsService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

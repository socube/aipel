package com.blackrubystudio.aipel3.sms;

import android.content.Context;
import android.util.Log;

import com.blackrubystudio.aipel3.api.gson.Category;
import com.blackrubystudio.aipel3.db.DatabaseHelper;
import com.blackrubystudio.aipel3.model.AccountingItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jaewoo on 2017. 1. 19..
 */

public class SmsCategoryReceiver implements Callback<Category> {

    private SmsObject smsObject;
    private DatabaseHelper dbHelper;
    private Context context;

    public SmsCategoryReceiver(SmsObject smsObject, Context context){
        this.smsObject = smsObject;
        this.context = context;
        if(dbHelper == null){
            dbHelper = new DatabaseHelper(context);
        }
    }

    @Override
    public void onFailure(Call<Category> call, Throwable t) {
        Log.d("expense", "SmsCategory onFailure");
    }

    @Override
    public void onResponse(Call<Category> call, Response<Category> response) {
        Log.d("expense", "SmsCategory onResponse");
        if (response.body().getPlace() == null){
            return;
        }
        if(response.body().getPlace().contains("알 수 없음")){
            return;
        }
        try {
            AccountingItem accountingItem = new AccountingItem(response.body().getPlace(),
                    response.body().getIndex(),
                    Integer.parseInt(smsObject.getPrice()),
                    Integer.parseInt(smsObject.getDate()),
                    Integer.parseInt(smsObject.getTime()));

            AddItem(accountingItem);
            Log.d("expense", "SmsCategory Received");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void AddItem(AccountingItem accountingItem){
        if(dbHelper == null){
            dbHelper = new DatabaseHelper(context);
        }
        dbHelper.insert(DatabaseHelper.Table.EXPENSE, accountingItem);
    }
}

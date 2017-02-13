package com.blackrubystudio.aipel3.sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jaewoo on 2017. 1. 19..
 */

public class SmsHelper {
    private Context context;

    public SmsHelper(Context context) {
        this.context = context;
    }

    public ArrayList<SmsObject> getSmsFromInbox(int startDate) {
        SmsParser smsParser = new SmsParser();
        ArrayList<SmsObject> smsObjects = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            String prev_address = "";
            boolean isBankSms = false;
            do {
                String cur_address = cursor.getString(cursor.getColumnIndex("address"));

                if (!cur_address.equals(prev_address)) {
                    isBankSms = SmsAddress.CheckAddress(cur_address);
                }

                if (isBankSms) {
                    String body = cursor.getString((cursor.getColumnIndex("body")));
                    Date date = new Date(cursor.getLong((cursor.getColumnIndex("date"))));
                    String formattedDate = new SimpleDateFormat("yyyyMMdd", Locale.US).format(date);

                    if (Integer.parseInt(formattedDate) < startDate) {
                        break;
                    }

                    String formattedTime = new SimpleDateFormat("HHmm", Locale.US).format(date);
                    smsObjects.add(smsParser.getSmsObject(body, formattedDate, formattedTime));
                }
            } while (cursor.moveToNext());
        }
        return smsObjects;
    }
}

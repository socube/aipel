package com.blackrubystudio.aipel3.sms;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsParser {
    private Pattern notSufficientPattern = Pattern.compile("(거절|취소|예정|할인)");
    private Pattern earnPattern = Pattern.compile("(입금)");
    private Pattern wonPattern = Pattern.compile("[\\d,.]+원");

    private Pattern sufficientPattern = Pattern.compile("발신|승인|출금");
    private Pattern cardNamePattern = Pattern.compile("체크|카드");
    private Pattern cardNumPattern = Pattern.compile("\\([0-9]\\*[0-9]\\*\\)");
    private Pattern paymentMethodPattern = Pattern.compile("일시불|잔액|누적");
    private Pattern userNamePattern = Pattern.compile("\\*");
    private Pattern datePattern = Pattern.compile("\\d{1,2}/\\d{1,2}");
    private Pattern timePattern = Pattern.compile("\\d{1,2}:\\d{1,2}");

    public SmsObject parse(Intent intent) {
        SmsObject smsObject = new SmsObject();

        // Check if bundle is null
        StringBuilder sms = new StringBuilder();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            String format = bundle.getString("format");
            SmsMessage[] messages = new SmsMessage[pdusObj.length];

            for(int i = 0; i < pdusObj.length; i++){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                }else {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                }
            }

            // get date & time
            Date date = new Date(messages[0].getTimestampMillis());
            String formattedDate = new SimpleDateFormat("yyyyMMdd", Locale.US).format(date);
            String formattedTime = new SimpleDateFormat("HHmm", Locale.US).format(date);

            // get address
            String smsAddress = messages[0].getOriginatingAddress();
            if(smsAddress.equals("15888100")){
                String customMessageForLotte = messages[0].getMessageBody().replaceFirst("롯데","");
                smsObject = getSmsObject(customMessageForLotte, formattedDate, formattedTime);
            }
            else if(SmsAddress.CheckAddress(smsAddress)){
                smsObject = getSmsObject(messages[0].getMessageBody(), formattedDate, formattedTime);
            }
        }
        return smsObject;
    }

    public SmsObject getSmsObject(String message, String date, String time) {
        SmsObject smsObject = new SmsObject();
        //Log.d("sms", message);

        if (message.isEmpty()) {
            return smsObject;
        }

        Matcher notSufficientMatcher = notSufficientPattern.matcher(message);
        if (notSufficientMatcher.find()) {
            return smsObject;
        }

        Matcher sufficientMatcher = sufficientPattern.matcher(message);
        if (!sufficientMatcher.find()) {
            return smsObject;
        }

        Matcher wonMatcher = wonPattern.matcher(message);
        if(!wonMatcher.find()){
            return smsObject;
        }

        // get date & time
        smsObject.setDate(date);
        smsObject.setTime(time);

        // get price
        Matcher earnMatcher = earnPattern.matcher(message);

        String wonString = message.substring(wonMatcher.start(), wonMatcher.end())
                .replace("원", "").replace(",", "");
        int won = Integer.valueOf(wonString);

        if (earnMatcher.find()) {
            smsObject.setPrice(Integer.toString(won));
        } else {
            smsObject.setPrice(Integer.toString(-won));
        }

        // get place
        String place = "";
        String[] words = message.split("\\n|\\r| ");
        for(String string : words){
            if(!sufficientPattern.matcher(string).find() && !cardNamePattern.matcher(string).find()
                    && !cardNumPattern.matcher(string).find() && !datePattern.matcher(string).find()
                    && !timePattern.matcher(string).find() && !timePattern.matcher(string).find()
                    && !wonPattern.matcher(string).find() && !paymentMethodPattern.matcher(string).find()
                    && !userNamePattern.matcher(string).find()){
                place += string;
            }
        }
        smsObject.setPlace(place);

        return smsObject;
    }
}

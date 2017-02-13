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
    private Pattern cardNumPattern = Pattern.compile("\\([0-9]\\*[0-9]\\*\\)");
    private Pattern cardNamePattern = Pattern.compile("(신한|우리|국민|씨티)(체크|카드)");

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
            if(SmsAddress.CheckAddress(smsAddress)){
                smsObject = getSmsObject(messages[0].getMessageBody(), formattedDate, formattedTime);
            }
        }
        return smsObject;
    }

    public SmsObject getSmsObject(String message, String date, String time){
        SmsObject smsObject = new SmsObject();
        Log.d("sms", message);
        if(!message.isEmpty()) {
            Matcher wonMatcher = wonPattern.matcher(message);
            Matcher notSufficientMatcher = notSufficientPattern.matcher(message);

            if (wonMatcher.find() && !notSufficientMatcher.find()){
                String wonString = message.substring(wonMatcher.start(), wonMatcher.end())
                        .replace("원","").replace(",","");
                int won = Integer.valueOf(wonString);

                //Matcher payMatcher = payPattern.matcher(message);
                Matcher earnMatcher = earnPattern.matcher(message);
                Matcher cardNumMatcher = cardNumPattern.matcher(message);
                Matcher cardNameMatcher = cardNamePattern.matcher(message);

                // 가격 추출 및 표시
                if(earnMatcher.find()){
                    smsObject.setPrice(Integer.toString(won));
                }else{
                    smsObject.setPrice(Integer.toString(-won));
                }

                // insert date & time
                smsObject.setDate(date);
                smsObject.setTime(time);

                // 카드 이름 추출 및 표시
                String cardName = "";
                if(cardNameMatcher.find()){
                    cardName += cardNameMatcher.group();
                }
                if(cardNumMatcher.find()){
                    String tmp = message.substring(cardNumMatcher.start(), cardNumMatcher.end());
                    cardName += tmp;
                }
                smsObject.setCardName(cardName);

                // 쓴곳 추출
                String[] words = message.split("\\n|\\r| ");
                int num = 0;
                for(String string : words){
                    num++;
                    if(string.contains("원")){
                        break;
                    }
                }
                String place="";
                for(int i = num; i<words.length; i++){
                    place += words[i];
                }
                smsObject.setPlace(place);
            }
        }
        return smsObject;
    }
}

package com.blackrubystudio.aipel3.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jaewoo on 2017. 1. 15..
 */

public class StandardFormat {

    private static String[] expenseArray = {
            "식비",
            "쇼핑",
            "문화/사교",
            "교육",
            "교통",
            "주거/통신",
            "기타"
    };

    private static String[] earnArray = {
            "주수입",
            "부수입",
            "이체",
            "기타"
    };

    public static String onCurrencyFormat(int num){
        return String.format(Locale.US, "%,d", num);
    }

    public static int removeCurrencyFormat(String textString){
        String cleanString = textString.replaceAll(",","");
        return Integer.parseInt(cleanString);
    }

    public static String onDateFormat(int year, int month, int day){
        return String.format(Locale.US, "%d년 %d월 %d일", year, month, day);
    }

    public static String onTimeFormat(int hour, int minute){
        return String.format(Locale.US, "%d시 %d분", hour, minute);
    }

    public static int getCurrentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        String DateString = dateFormat.format(new Date());
        return Integer.parseInt(DateString);
    }

    public static int getMonthlySavingBudget(int dueDate, int currentBudget, int targetBudget){
        int currentDate = StandardFormat.getCurrentDate();
        int dailySaving = (targetBudget - currentBudget) / getDeltaDate(currentDate, dueDate);
        int currentMonth = (currentDate/100) % 100;

        if(currentMonth == ((dueDate/100)%100)){
            // less than a month
            return ((dueDate%100) - (currentDate%100)) * dailySaving;
        }else{
            // more than a month
            int days;
            switch (currentMonth){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    days = 31;
                    break;
                case 2:
                    days = 28;
                    break;
                default:
                    days = 30;
                    break;
            }
            return days * dailySaving;
        }
    }

    public static int getDeltaDate(int currentDate, int dueDate){
        int deltaDays = 0;
        int currentMonth = (currentDate / 100) % 100;
        int dueMonth = (dueDate / 100) % 100;
        int days;

        if(currentMonth == dueMonth){
            deltaDays = dueDate - currentDate;
        }else{
            for(int i = currentMonth; i < dueMonth; i++){
                switch (i){
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        days = 31;
                        break;
                    case 2:
                        days = 28;
                        break;
                    default:
                        days = 30;
                        break;
                }
                if(i == currentMonth){
                    days -= currentDate % 100 + 1;
                }
                deltaDays += days;
            }
            deltaDays += (dueDate%100) - 1;
        }
        return deltaDays;
    }

    public static int getCategoryNum(String stringFromServer, boolean isExpense){

        int length;

        if(isExpense){
            length = expenseArray.length;
            for(int i =0; i < length; i++){
                if(stringFromServer.contains(expenseArray[i])){
                    return i;
                }
            }
            return 6;
        }else{
            length = earnArray.length;
            for(int i =0; i < length; i++){
                if(stringFromServer.contains(earnArray[i])){
                    return i;
                }
            }
            return 3;
        }
    }
}

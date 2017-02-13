package com.blackrubystudio.aipel3.sms;

public class SmsObject {
    private String place = "알 수 없음";
    private String price = "알 수 없음";
    private String date = "알 수 없음"; // yyyyMMdd
    private String time = "알 수 없음"; // hhMM
    private String cardName = "알 수 없음";

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}

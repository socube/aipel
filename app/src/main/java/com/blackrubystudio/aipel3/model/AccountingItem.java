package com.blackrubystudio.aipel3.model;

/**
 * Created by jaewoo on 2016. 12. 29..
 */

public class AccountingItem {

    private int id;
    private String place; // place name
    private String index; // category name
    private int integerIndex; // category integer
    private int money;
    private int date; // format: xxxx년 xx월 xx일, 8글자
    private int time; // format: xx시 xx분, 4글자

    public AccountingItem(){}

    public AccountingItem(String place, String index, int money, int date, int time){
        this.place = place;
        this.index = index;
        this.money = money;
        this.date = date;
        this.time =time;
        this.integerIndex = -1;
    }

    public String getIndex() {
        return index;
    }

    public int getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }

    public String getPlace() {
        return place;
    }

    public int getTime() {
        return time;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getIntegerIndex() {
        return integerIndex;
    }

    public void setIntegerIndex(int integerIndex) {
        this.integerIndex = integerIndex;
    }
}

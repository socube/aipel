package com.blackrubystudio.aipel3.model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by jaewoo on 2017. 1. 5..
 */

public class ExpenseItem implements Parent<AccountingItem> {

    private int date;
    private List accountingItemList;

    public ExpenseItem(int date, List accountingItemList){
        this.date = date;
        this.accountingItemList = accountingItemList;
    }

    public int getDate() {
        return date;
    }

    @Override
    public List<AccountingItem> getChildList() {
        return accountingItemList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }

}

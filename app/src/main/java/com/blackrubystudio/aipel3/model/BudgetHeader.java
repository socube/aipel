package com.blackrubystudio.aipel3.model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by jaewoo on 2017. 1. 6..
 */

public class BudgetHeader implements Parent<TargetBudgetItem> {

    private String header;
    private List targetBudgetItems;

    public BudgetHeader(String name, List targetBudgetItems){
        this.header = name;
        this.targetBudgetItems = targetBudgetItems;
    }

    public String getHeader() {
        return header;
    }

    @Override
    public List<TargetBudgetItem> getChildList() {
        return targetBudgetItems;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }
}

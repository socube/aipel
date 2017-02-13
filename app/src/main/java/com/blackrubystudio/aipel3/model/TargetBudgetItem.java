package com.blackrubystudio.aipel3.model;

/**
 * Created by jaewoo on 2017. 1. 6..
 */

public class TargetBudgetItem {

    private int id;
    private String name;
    private int isGoal;
    private int currentBudget;
    private int targetBudget;
    private int dueDate; //format yyyyMMdd
    private int monthlyBudget;

    public TargetBudgetItem(){}

    public TargetBudgetItem(String name, int isGoal, int currentBudget, int targetBudget, int dueDate, int monthlyBudget) {
        this.name = name;
        this.isGoal = isGoal;
        this.currentBudget = currentBudget;
        this.targetBudget = targetBudget;
        this.dueDate = dueDate;
        this.monthlyBudget = monthlyBudget;
    }

    public int getCurrentBudget() {
        return currentBudget;
    }

    public void setCurrentBudget(int currentBudget) {
        this.currentBudget = currentBudget;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int isGoal() {
        return isGoal;
    }

    public void setGoal(int goal) {
        isGoal = goal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTargetBudget() {
        return targetBudget;
    }

    public void setTargetBudget(int targetBudget) {
        this.targetBudget = targetBudget;
    }

    public int getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(int monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }
}

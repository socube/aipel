package com.blackrubystudio.aipel3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blackrubystudio.aipel3.model.AccountingItem;
import com.blackrubystudio.aipel3.model.TargetBudgetItem;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String TAG = getClass().getName();

    private static final String DATABASE_NAME = "aipel";

    public enum Table {
        EXPENSE,
        BUDGET
    }

    private static final int DATABASE_VERSION = 10;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create(db, Models.EXPENSE);
        create(db, Models.BUDGET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ Table.EXPENSE.name());
        db.execSQL("DROP TABLE IF EXISTS "+ Table.BUDGET.name());
        onCreate(db);
    }

    public void create(SQLiteDatabase database, String scheme) {
        database.execSQL(scheme);
    }

    public void insert(Table table, Object object){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        switch (table){
            case EXPENSE:
                AccountingItem accountingItem = (AccountingItem) object;
                values.put("CATEGORY", accountingItem.getIndex());
                values.put("PLACE", accountingItem.getPlace());
                values.put("PRICE", accountingItem.getMoney());
                values.put("_TIME", accountingItem.getTime());
                values.put("_DATE", accountingItem.getDate());
                values.put("CATEGORYNUM", accountingItem.getIntegerIndex());
                break;
            case BUDGET:
                TargetBudgetItem targetBudgetItem = (TargetBudgetItem) object;
                values.put("NAME", targetBudgetItem.getName());
                values.put("IS_GOAL", targetBudgetItem.isGoal());
                values.put("CURRENT_BUDGET", targetBudgetItem.getCurrentBudget());
                values.put("TARGET_BUDGET", targetBudgetItem.getTargetBudget());
                values.put("DUE_DATE", targetBudgetItem.getDueDate());
                values.put("MONTHLY_BUDGET", targetBudgetItem.getMonthlyBudget());
                break;
            default:
                return;
        }

        db.insert(table.name(), null, values);
    }

    // SELECT * FROM table
    public ArrayList<Object> select(Table table){
        ArrayList<Object> objectArrayList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor;
        String sql;

        switch (table) {
            case EXPENSE:
                sql = "SELECT * FROM expense ORDER BY _DATE DESC, _TIME DESC";
                cursor = database.rawQuery(sql, null);
                break;
            case BUDGET:
                sql = "SELECT * FROM budget ORDER BY IS_GOAL ASC, DUE_DATE ASC";
                cursor = database.rawQuery(sql, null);
                break;
            default:
                cursor = null;
                break;
        }

        if(cursor != null && cursor.moveToFirst()){
            do {
                Object object = getItemFromCursor(table, cursor);
                objectArrayList.add(object);
            } while (cursor.moveToNext());
        }
        return objectArrayList;
    }

    // SELECT * FROM table WHERE id = ?
    public Object select(Table table, int id){
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT * FROM " + table.name() + " WHERE _id = " + id;

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return getItemFromCursor(table, cursor);
    }

    public void update(Table table, Object object){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String string = "_id = ?";

        switch (table) {
            case EXPENSE:
                AccountingItem accountingItem = (AccountingItem) object;
                values.put("CATEGORY", accountingItem.getIndex());
                values.put("PLACE", accountingItem.getPlace());
                values.put("PRICE", accountingItem.getMoney());
                values.put("_DATE", accountingItem.getDate());
                values.put("_TIME", accountingItem.getTime());
                values.put("CATEGORYNUM", accountingItem.getIntegerIndex());
                db.update(table.name(), values, string, new String[]{ String.valueOf(accountingItem.getId())});
                break;

            case BUDGET:
                TargetBudgetItem targetBudgetItem = (TargetBudgetItem) object;
                values.put("NAME", targetBudgetItem.getName());
                values.put("IS_GOAL", targetBudgetItem.isGoal());
                values.put("CURRENT_BUDGET", targetBudgetItem.getCurrentBudget());
                values.put("TARGET_BUDGET", targetBudgetItem.getTargetBudget());
                values.put("DUE_DATE", targetBudgetItem.getDueDate());
                values.put("MONTHLY_BUDGET", targetBudgetItem.getMonthlyBudget());
                db.update(table.name(), values, string, new String[]{ String.valueOf(targetBudgetItem.getId())});
                break;

            default:
                break;
        }
    }

    public void delete(Table table, int id){
        String string = "_id = ?";
        getWritableDatabase().delete(table.name(), string, new String[]{String.valueOf(id)});
    }

    public void delete(Table table){
        getWritableDatabase().execSQL("DELETE FROM "+table.name());
    }

    public void drop(Table table){
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + table.name());
    }

    private Object getItemFromCursor(Table table, Cursor cursor) {

        switch (table) {
            case EXPENSE:
                AccountingItem accountingItem = new AccountingItem();
                accountingItem.setId(cursor.getInt(0));
                accountingItem.setIndex(cursor.getString(1));
                accountingItem.setPlace(cursor.getString(2));
                accountingItem.setMoney(cursor.getInt(3));
                accountingItem.setDate(cursor.getInt(4));
                accountingItem.setTime(cursor.getInt(5));
                accountingItem.setIntegerIndex(cursor.getInt(6));
                return accountingItem;

            case BUDGET:
                TargetBudgetItem targetBudgetItem = new TargetBudgetItem();
                targetBudgetItem.setId(cursor.getInt(0));
                targetBudgetItem.setName(cursor.getString(1));
                targetBudgetItem.setGoal(cursor.getInt(2));
                targetBudgetItem.setCurrentBudget(cursor.getInt(3));
                targetBudgetItem.setTargetBudget(cursor.getInt(4));
                targetBudgetItem.setDueDate(cursor.getInt(5));
                targetBudgetItem.setMonthlyBudget(cursor.getInt(6));
                return targetBudgetItem;

            default:
                return null;

        }
    }
}

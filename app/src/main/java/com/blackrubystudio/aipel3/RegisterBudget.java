package com.blackrubystudio.aipel3;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.blackrubystudio.aipel3.db.DatabaseHelper;
import com.blackrubystudio.aipel3.model.BudgetHeader;
import com.blackrubystudio.aipel3.model.TargetBudgetItem;
import com.blackrubystudio.aipel3.util.BudgetTextWatcher;
import com.blackrubystudio.aipel3.util.StandardFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaewoo on 2017. 1. 15..
 */

public class RegisterBudget extends AppCompatActivity {

    @BindView(R.id.register_toolbar) Toolbar toolbar;
    @BindView(R.id.register_fixed_budget_place) EditText placeEditText;
    @BindView(R.id.register_fixed_budget_expense) EditText expenseEditText;
    @BindView(R.id.register_fixed_budget_day) EditText dayEditText;
    @BindView(R.id.register_fixed_budget_date) TextView dateTextView;
    @BindView(R.id.fixed_budget_1_q) TextView textView1_q;
    @BindView(R.id.fixed_budget_2_q) TextView textView2_q;
    @BindView(R.id.fixed_budget_3_q) TextView textView3_q;


    DatabaseHelper dbHelper;
    int _id; // _id > -1 -> from database
    boolean isFixed;
    int year = 2017, month = 12, day = 31;
    int currentBudget = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_budget);
        ButterKnife.bind(this);

        // Initialize database
        if(dbHelper == null){
            dbHelper = new DatabaseHelper(getApplicationContext());
        }

        // initialize toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // get data from intent or dataBase
        Intent intent = getIntent();
        _id = intent.getIntExtra("id", -1);
        isFixed = intent.getBooleanExtra("isFixed", false);

        // insert data to view
        if(_id > -1){
            TargetBudgetItem targetBudgetItem = (TargetBudgetItem) dbHelper.select(DatabaseHelper.Table.BUDGET, _id);

            placeEditText.setText(targetBudgetItem.getName());
            if(isFixed) {
                expenseEditText.setText(StandardFormat.onCurrencyFormat(targetBudgetItem.getCurrentBudget()));
                dayEditText.setText(String.valueOf(targetBudgetItem.getDueDate()));
            }else{
                int dueDate = targetBudgetItem.getDueDate();
                year = dueDate / 10000;
                month = (dueDate / 100) % 100;
                day = dueDate % 100;
                currentBudget = targetBudgetItem.getCurrentBudget();

                expenseEditText.setText(StandardFormat.onCurrencyFormat(targetBudgetItem.getTargetBudget()));
                dateTextView.setText(StandardFormat.onDateFormat(year, month, day));
            }
        }

        // initialize view
        if(isFixed){
            getSupportActionBar().setTitle("고정지출 등록");
            dayEditText.setVisibility(View.VISIBLE);
            dateTextView.setVisibility(View.GONE);

            textView1_q.setText(R.string.fixed_budget_1_q);
            textView2_q.setText(R.string.fixed_budget_2_q);
            textView3_q.setText(R.string.fixed_budget_3_q);
        }else {
            getSupportActionBar().setTitle("목표관리 등록");
            dayEditText.setVisibility(View.GONE);
            dateTextView.setVisibility(View.VISIBLE);

            textView1_q.setText(R.string.target_budget_1_q);
            textView2_q.setText(R.string.target_budget_2_q);
            textView3_q.setText(R.string.target_budget_3_q);
        }

        BudgetTextWatcher textWatcher = new BudgetTextWatcher(expenseEditText);
        expenseEditText.addTextChangedListener(textWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_delete) {
            onDeleteButtonClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
        onClick
     */

    @OnClick(R.id.register_fixed_budget_button)
    public void onButtonClicked(){
        if (!validateForm()) {
            return;
        }

        if(dbHelper == null){
            dbHelper = new DatabaseHelper(getApplicationContext());
        }

        TargetBudgetItem targetBudgetItem = new TargetBudgetItem();
        targetBudgetItem.setName(placeEditText.getText().toString());
        int expense = StandardFormat.removeCurrencyFormat(expenseEditText.getText().toString());

        if(isFixed){
            targetBudgetItem.setCurrentBudget(expense);
            targetBudgetItem.setGoal(0);
            targetBudgetItem.setTargetBudget(0);
            targetBudgetItem.setDueDate(Integer.parseInt(dayEditText.getText().toString()));
            targetBudgetItem.setMonthlyBudget(0);
        }else{
            int dueDate = year*10000 + month*100 + day;

            targetBudgetItem.setCurrentBudget(currentBudget);
            targetBudgetItem.setGoal(1);
            targetBudgetItem.setTargetBudget(expense);
            targetBudgetItem.setDueDate(dueDate);
            targetBudgetItem.setMonthlyBudget(StandardFormat.getMonthlySavingBudget(dueDate, 0, expense));
        }

        if(_id > -1){
            targetBudgetItem.setId(_id);
            dbHelper.update(DatabaseHelper.Table.BUDGET, targetBudgetItem);
        }else{
            dbHelper.insert(DatabaseHelper.Table.BUDGET, targetBudgetItem);
        }

        finish();
    }

    public void onDeleteButtonClicked(){
        if(dbHelper == null){
            dbHelper = new DatabaseHelper(getApplicationContext());
        }

        if(_id > -1){
            dbHelper.delete(DatabaseHelper.Table.BUDGET, _id);
        }
        finish();
    }

    @OnClick(R.id.register_fixed_budget_date)
    public void onDayEditTextClicked(){
        new DatePickerDialog(RegisterBudget.this, dateSetListener, year, month-1, day).show();
    }

    private boolean validateForm(){
        boolean result = true;

        String place = placeEditText.getText().toString();
        String expense = expenseEditText.getText().toString();

        if(place.isEmpty()){
            placeEditText.setError("입력이 필요합니다.");
            result = false;
        }

        if(expense.isEmpty()){
            expenseEditText.setError("입력이 필요합니다.");
            result = false;
        }else if(expense.length() > 9){
            expenseEditText.setError("입력 가능 액수를 초과합니다.");
            result = false;
        }

        if(isFixed) {
            String day = dayEditText.getText().toString();
            if (day.isEmpty()) {
                dayEditText.setError("입력이 필요합니다.");
                result = false;
            }
            int dayNum = Integer.parseInt(day);
            if (dayNum < 0 || dayNum > 31) {
                dayEditText.setError("1~31 사이의 수를 입력해주세요.");
                result = false;
            }
        }

        return result;
    }

    /**
        DatePicker
     */

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateTextView.setText(StandardFormat.onDateFormat(year, monthOfYear+1, dayOfMonth));
            changeDate(year, monthOfYear+1, dayOfMonth);
        }
    };

    private void changeDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }
}

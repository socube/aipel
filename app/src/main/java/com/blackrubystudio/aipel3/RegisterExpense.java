package com.blackrubystudio.aipel3;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.blackrubystudio.aipel3.db.DatabaseHelper;
import com.blackrubystudio.aipel3.model.AccountingItem;
import com.blackrubystudio.aipel3.model.TargetBudgetItem;
import com.blackrubystudio.aipel3.util.BudgetTextWatcher;
import com.blackrubystudio.aipel3.util.StandardFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaewoo on 2017. 1. 16..
 */

public class RegisterExpense extends AppCompatActivity {
    private static String TAG = "RegisterExpense";

    @BindView(R.id.register_expense_toolbar) Toolbar toolbar;
    @BindView(R.id.register_expense_place) EditText placeEditText;
    @BindView(R.id.register_expense_expense) EditText expenseEditText;
    @BindView(R.id.register_expense_date) TextView dateTextView;
    @BindView(R.id.register_expense_time) TextView timeTextView;
    @BindView(R.id.register_expense_1_q) TextView questionTextView_1;
    @BindView(R.id.register_expense_3_q) TextView questionTextView_3;
    @BindView(R.id.register_expense_4_q) TextView questionTextView_4;
    @BindView(R.id.register_radio_earn) RadioGroup earnRadioGroup;  // id: 1~4
    @BindView(R.id.register_radio_expense_1) RadioGroup expenseRadioGroup_1; // id: 5~8
    @BindView(R.id.register_radio_expense_2) RadioGroup expenseRadioGroup_2; // id: 9~11

    DatabaseHelper dbHelper;
    int _id; // _id > -1 -> from database
    boolean isExpense = false;
    int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_expense);
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

        // initialize view
        initializeView();
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
        on click
     */
    @OnClick(R.id.register_expense_date)
    public void onDateClicked(){
        new DatePickerDialog(RegisterExpense.this, dateSetListener, year, month, day).show();
    }

    @OnClick(R.id.register_expense_time)
    public void onTimeClicked(){
        new TimePickerDialog(RegisterExpense.this, timeSetListener, hour, minute, false).show();
    }

    @OnClick(R.id.register_expense_button)
    public void onButtonClicked(){
        if (!validateForm()) {
            return;
        }

        if(dbHelper == null){
            dbHelper = new DatabaseHelper(getApplicationContext());
        }

        int categoryNum = getRadioButtonId();
        String categoryString="";
        if(categoryNum > 0 && categoryNum < 5) {
            String[] categoryArray= getResources().getStringArray(R.array.earn_category);
            categoryString = categoryArray[categoryNum-1];
        }else if(categoryNum > 4 && categoryNum < 12) {
            String[] categoryArray= getResources().getStringArray(R.array.expense_category);
            categoryString = categoryArray[categoryNum-5];
        }

        int money;
        if(isExpense){
            money = -(StandardFormat.removeCurrencyFormat(expenseEditText.getText().toString()));
        }else{
            money = StandardFormat.removeCurrencyFormat(expenseEditText.getText().toString());
        }

        AccountingItem accountingItem = new AccountingItem();
        accountingItem.setPlace(placeEditText.getText().toString());
        accountingItem.setIndex(categoryString);
        accountingItem.setMoney(money);
        accountingItem.setDate(year * 10000 + month * 100 + day);
        accountingItem.setTime(hour * 100 + minute);

        Log.d(TAG,"_id: "+_id);
        if(_id > -1){
            accountingItem.setId(_id);
            dbHelper.update(DatabaseHelper.Table.EXPENSE, accountingItem);
        }else{
            dbHelper.insert(DatabaseHelper.Table.EXPENSE, accountingItem);
        }

        finish();
    }

    public void onDeleteButtonClicked(){
        if(dbHelper == null){
            dbHelper = new DatabaseHelper(getApplicationContext());
        }

        Log.d(TAG,"_id: "+_id);
        if(_id > -1){
            dbHelper.delete(DatabaseHelper.Table.EXPENSE, _id);
        }
        finish();
    }

    /*
        arrange method
     */
    private void initializeView(){
        // get data from intent or dataBase
        Intent intent = getIntent();
        _id = intent.getIntExtra("id", -1);
        isExpense = intent.getBooleanExtra("isExpense", false);

        if(_id > -1){
            AccountingItem accountingItem = (AccountingItem) dbHelper.select(DatabaseHelper.Table.EXPENSE, _id);

            int _date = accountingItem.getDate();
            int _time = accountingItem.getTime();
            int money = accountingItem.getMoney();
            int categoryNum = accountingItem.getIntegerIndex();

            if(money < 0){
                isExpense = true;
                money = -money;
            }

            year = _date / 10000;
            month = (_date / 100) % 100;
            day = _date % 100;
            hour = _time / 100;
            minute = _time % 100;

            placeEditText.setText(accountingItem.getPlace());
            expenseEditText.setText(StandardFormat.onCurrencyFormat(money));
            BudgetTextWatcher textWatcher = new BudgetTextWatcher(expenseEditText);
            expenseEditText.addTextChangedListener(textWatcher);

            // set category
            if(isExpense){
                if(categoryNum > -1 && categoryNum < 4){
                    ((RadioButton)expenseRadioGroup_1.getChildAt(categoryNum)).setChecked(true);
                }else if(categoryNum > 3 && categoryNum < 7){
                    expenseRadioGroup_1.clearCheck();
                    ((RadioButton)expenseRadioGroup_2.getChildAt(categoryNum-4)).setChecked(true);
                }
            }else{
                if(categoryNum > -1 && categoryNum < 4) {
                    ((RadioButton)earnRadioGroup.getChildAt(categoryNum)).setChecked(true);
                }
            }
        }else{
            // get current date
            GregorianCalendar calendar = new GregorianCalendar();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH)+1;
            day= calendar.get(Calendar.DAY_OF_MONTH);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }
        dateTextView.setText(StandardFormat.onDateFormat(year, month, day));
        timeTextView.setText(StandardFormat.onTimeFormat(hour, minute));

        if(isExpense){
            getSupportActionBar().setTitle("지출 상세 내역");
            expenseRadioGroup_1.setVisibility(View.VISIBLE);
            expenseRadioGroup_2.setVisibility(View.VISIBLE);
            earnRadioGroup.setVisibility(View.GONE);

            expenseRadioGroup_1.setOnCheckedChangeListener(listener1);
            expenseRadioGroup_2.setOnCheckedChangeListener(listener2);
            questionTextView_1.setText(R.string.expense_1_q);
            questionTextView_3.setText(R.string.expense_3_q);
            questionTextView_4.setText(R.string.expense_4_q);
            placeEditText.setHint(R.string.fixed_budget_1_a);
        }else {
            getSupportActionBar().setTitle("수입 상세 내역");
            expenseRadioGroup_1.setVisibility(View.GONE);
            expenseRadioGroup_2.setVisibility(View.GONE);
            earnRadioGroup.setVisibility(View.VISIBLE);

            questionTextView_1.setText(R.string.earn_1_q);
            questionTextView_3.setText(R.string.earn_3_q);
            questionTextView_4.setText(R.string.earn_4_q);
            placeEditText.setHint(R.string.earn_1_a);
        }
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
        return result;
    }

    /*
        listener
     */
    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(i != -1){
                expenseRadioGroup_2.setOnCheckedChangeListener(null);
                expenseRadioGroup_2.clearCheck();
                expenseRadioGroup_2.setOnCheckedChangeListener(listener2);
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(i != -1){
                expenseRadioGroup_1.setOnCheckedChangeListener(null);
                expenseRadioGroup_1.clearCheck();
                expenseRadioGroup_1.setOnCheckedChangeListener(listener1);
            }
        }
    };

    private int getRadioButtonId(){
        int id1 = expenseRadioGroup_1.getCheckedRadioButtonId();
        int id2 = expenseRadioGroup_2.getCheckedRadioButtonId();
        int num = id1 == -1 ? id2 : id1;
        if(num == -1){
            num = earnRadioGroup.getCheckedRadioButtonId();
        }
        if(num % 11 !=0){
            return num % 11;
        }else{
            return 11;
        }
    }

    /*
        DatePicker & TimePicker
     */

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateTextView.setText(StandardFormat.onDateFormat(year,monthOfYear+1, dayOfMonth));
            changeDate(year, monthOfYear+1, dayOfMonth);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            timeTextView.setText(StandardFormat.onTimeFormat(hourOfDay, minute));
            changeTime(hourOfDay, minute);
        }
    };

    private void changeDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    private void changeTime(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
    }
}

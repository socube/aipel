package com.blackrubystudio.aipel3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackrubystudio.aipel3.util.CircleAnimIndicator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaewoo on 2017. 1. 17..
 */

public class SurveyActivity extends AppCompatActivity {
    private static final String PREFERENCE = "preference";

    @BindView(R.id.activity_survey_circle) CircleAnimIndicator circleAnimIndicator;
    @BindView(R.id.fragment_survey_text) TextView textView;
    @BindView(R.id.fragment_survey_name) EditText nameEditText;
    @BindView(R.id.fragment_survey_sex) LinearLayout linearLayout;
    @BindView(R.id.fragment_survey_age) EditText ageEditText;
    @BindView(R.id.fragment_survey_available_budget) EditText budgetEditText;
    @BindView(R.id.fragment_survey_man) TextView manTextView;
    @BindView(R.id.fragment_survey_woman) TextView womanTextView;

    int pageNum;
    boolean isMan = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        pageNum = intent.getIntExtra("page",0);

        // initialize Indicator
        circleAnimIndicator.setItemMargin(15);
        circleAnimIndicator.setAnimDuration(300);
        circleAnimIndicator.createDotPanel(3, R.drawable.wizard_indicator_off, R.drawable.wizard_indicator_on);
        circleAnimIndicator.selectDot(pageNum);

        // initialize view
        textView.setText(R.string.survey_1+pageNum);

        switch (pageNum){
            case 0:
                nameEditText.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                ageEditText.setVisibility(View.GONE);
                budgetEditText.setVisibility(View.GONE);
                break;
            case 1:
                nameEditText.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                ageEditText.setVisibility(View.VISIBLE);
                budgetEditText.setVisibility(View.GONE);
                break;
            case 2:
                nameEditText.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                ageEditText.setVisibility(View.GONE);
                budgetEditText.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick(R.id.activity_survey_next)
    public void onNextClicked(){
        boolean result = true;

        switch (pageNum){
            case 0:
                String name = nameEditText.getText().toString();
                if (name.isEmpty()) {
                    nameEditText.setError("정보가 필요합니다.");
                    result = false;
                }else{
                    nameEditText.setError(null);
                }
                break;
            case 1:
                String age = ageEditText.getText().toString();
                if (age.isEmpty()) {
                    ageEditText.setError("정보가 필요합니다.");
                    result = false;
                }else if (age.length() > 3) {
                    nameEditText.setError("0~100사이의 수를 입력해주세요.");
                    result = false;
                }else{
                    ageEditText.setError(null);
                }
                break;
            case 2:
                String budget = budgetEditText.getText().toString();
                if (budget.isEmpty()){
                    budgetEditText.setError("정보가 필요합니다.");
                    result = false;
                }else if (budget.length() > 8){
                    budgetEditText.setError("입력 가능 액수를 초과합니다.");
                    result = false;
                }else{
                    budgetEditText.setError(null);
                }
                break;
        }

        if(!result){
            return;
        }

        SharedPreferences pref = this.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if(pageNum < 2){
            if(pageNum==0){
                editor.putString("name", nameEditText.getText().toString());
            }else{
                editor.putInt("age", Integer.parseInt(ageEditText.getText().toString()));
                editor.putBoolean("isMan", isMan);
            }
            editor.apply();

            Intent intent = new Intent(SurveyActivity.this, SurveyActivity.class);
            intent.putExtra("page", pageNum+1);
            startActivity(intent);
        }else{
            editor.putInt("total_budget", Integer.parseInt(budgetEditText.getText().toString()));
            editor.putBoolean("hasInfo", true);
            editor.apply();

            Intent intent = new Intent(SurveyActivity.this, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.activity_survey_before)
    public void onBeforeClicked(){
        if(pageNum > 0){
            finish();
        }
    }

    @OnClick(R.id.fragment_survey_man)
    public void onManClicked(){
        manTextView.setTypeface(null, Typeface.BOLD);
        manTextView.setTextColor(getResources().getColor(R.color.white));
        manTextView.setTextSize(20);
        womanTextView.setTypeface(null, Typeface.NORMAL);
        womanTextView.setTextColor(getResources().getColor(R.color.gray));
        womanTextView.setTextSize(16);
        isMan = true;
    }

    @OnClick(R.id.fragment_survey_woman)
    public void onWomanClicked(){
        manTextView.setTypeface(null, Typeface.NORMAL);
        manTextView.setTextColor(getResources().getColor(R.color.gray));
        manTextView.setTextSize(16);
        womanTextView.setTypeface(null, Typeface.BOLD);
        womanTextView.setTextColor(getResources().getColor(R.color.white));
        womanTextView.setTextSize(20);
        isMan = false;
    }

}

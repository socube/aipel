package com.blackrubystudio.aipel3.util;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackrubystudio.aipel3.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jaewoo on 2017. 1. 6..
 */

public class DialogSafeToSpendUtils {

    private static final String PREFERENCE = "preference";

    private Fragment fragment;
    private Dialog dialog;
    private Button button;
    private boolean isExpense;

    private TextView total_budget, fixed_budget, target_budget, safe_to_spend, monthly_expense, current_safe_to_spend;
    private LinearLayout linearLayout;

    public DialogSafeToSpendUtils(Fragment fragment, boolean isExpense){
        this.fragment = fragment;
        this.isExpense = isExpense;

        if(dialog == null){
            dialog = new Dialog(fragment.getContext(), R.style.CustomDialogTheme);
        }
        dialog.setContentView(R.layout.dialog_safe_to_spend);
        dialog.setCancelable(true);

        total_budget = (TextView) dialog.findViewById(R.id.dialog_total_budget);
        fixed_budget = (TextView) dialog.findViewById(R.id.dialog_fixed_budget);
        target_budget = (TextView) dialog.findViewById(R.id.dialog_target_budget);
        safe_to_spend = (TextView) dialog.findViewById(R.id.dialog_safe_to_spend);
        button = (Button) dialog.findViewById(R.id.dialog_safe_to_spend_ok);
        linearLayout = (LinearLayout) dialog.findViewById(R.id.dialog_linearLayout);

        if(isExpense){
            monthly_expense = (TextView) dialog.findViewById(R.id.dialog_current_monthly_expense);
            current_safe_to_spend = (TextView) dialog.findViewById(R.id.dialog_current_safeToSpend);
        }
    }

    public void showDialog(){
        SharedPreferences pref = fragment.getActivity().getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        total_budget.setText(StandardFormat.onCurrencyFormat(pref.getInt("total_budget", 0)));
        fixed_budget.setText(StandardFormat.onCurrencyFormat(pref.getInt("fixed_budget", 0)));
        target_budget.setText(StandardFormat.onCurrencyFormat(pref.getInt("target_budget", 0)));
        int safe_to_spend_int = pref.getInt("safe_to_spend", 0);
        safe_to_spend.setText(StandardFormat.onCurrencyFormat(safe_to_spend_int));

        if(isExpense){
            linearLayout.setVisibility(View.VISIBLE);
            int monthly_expense_int = pref.getInt("current_month_expense", 0);
            if(monthly_expense_int > 0){
                monthly_expense.setTextColor(Color.BLACK);
                monthly_expense.setText(StandardFormat.onCurrencyFormat(monthly_expense_int));
            }else{
                monthly_expense.setTextColor(fragment.getResources().getColor(R.color.redText));
                monthly_expense.setText(StandardFormat.onCurrencyFormat(-monthly_expense_int));
            }
            current_safe_to_spend.setText(StandardFormat.onCurrencyFormat(safe_to_spend_int + monthly_expense_int));
        }else{
            linearLayout.setVisibility(View.GONE);
        }

        dialog.show();
        initDialogButtons();
    }

    private void initDialogButtons(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}

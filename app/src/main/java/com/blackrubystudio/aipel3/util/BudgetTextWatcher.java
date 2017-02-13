package com.blackrubystudio.aipel3.util;

import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blackrubystudio.aipel3.model.BudgetHeader;

import java.util.Locale;

/**
 * Created by jaewoo on 2017. 1. 13..
 */

public class BudgetTextWatcher implements TextWatcher{

    private EditText mEditText;
    private String currentString = "";

    public BudgetTextWatcher(EditText editText){
        this.mEditText = editText;
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        String textString = charSequence.toString();

        if(!textString.equals(currentString)){
            if(textString.length() > 9){
                mEditText.setTextColor(Color.RED);
            }else if(textString.length() < 1) {
                return;
            }else {
                mEditText.setTextColor(Color.BLACK);
            }

            mEditText.removeTextChangedListener(this);

            int parsed = StandardFormat.removeCurrencyFormat(textString);
            currentString = StandardFormat.onCurrencyFormat(parsed);

            mEditText.setText(currentString);
            mEditText.setSelection(currentString.length());
            mEditText.addTextChangedListener(this);
        }

    }
}

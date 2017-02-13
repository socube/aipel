package com.blackrubystudio.aipel3.viewholder;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.blackrubystudio.aipel3.R;
import com.blackrubystudio.aipel3.model.ExpenseItem;
import com.blackrubystudio.aipel3.util.StandardFormat;

import java.util.Locale;

/**
 * Created by jaewoo on 2017. 1. 5..
 */

public class ExpenseItemViewHolder extends ParentViewHolder{

    private TextView dateTextView;

    public ExpenseItemViewHolder(View itemView){
        super(itemView);
        dateTextView = (TextView) itemView.findViewById(R.id.item_tab_activity_head_textView);
    }

    public void bind(ExpenseItem expenseItem){
        int date = expenseItem.getDate();
        dateTextView.setText(StandardFormat.onDateFormat(date / 10000, (date / 100) % 100, date % 100));
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
    }
}

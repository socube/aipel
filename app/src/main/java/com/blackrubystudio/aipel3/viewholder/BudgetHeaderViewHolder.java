package com.blackrubystudio.aipel3.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.blackrubystudio.aipel3.R;
import com.blackrubystudio.aipel3.model.BudgetHeader;

/**
 * Created by jaewoo on 2017. 1. 7..
 */

public class BudgetHeaderViewHolder extends ParentViewHolder{

    private TextView headerTextView;

    public BudgetHeaderViewHolder(View itemView){
        super(itemView);
        Log.d("BudgetHeaderViewHolder", "onCreated");
        headerTextView = (TextView) itemView.findViewById(R.id.item_tab_activity_head_textView);
    }

    public void bind(BudgetHeader budgetHeader){
        Log.d("BudgetHeaderViewHolder", "bind: "+budgetHeader.getHeader());
        headerTextView.setText(budgetHeader.getHeader());
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

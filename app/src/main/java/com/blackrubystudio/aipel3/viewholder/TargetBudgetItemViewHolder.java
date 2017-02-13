package com.blackrubystudio.aipel3.viewholder;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.blackrubystudio.aipel3.R;
import com.blackrubystudio.aipel3.adapter.BudgetRecyclerViewAdapter;
import com.blackrubystudio.aipel3.model.TargetBudgetItem;
import com.blackrubystudio.aipel3.util.OnItemClickListener;
import com.blackrubystudio.aipel3.util.StandardFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jaewoo on 2017. 1. 7..
 */

public class TargetBudgetItemViewHolder extends ChildViewHolder
        implements View.OnClickListener{

    private int viewType;
    private TextView nameTextView;
    private TextView currentBudgetTextView;
    private TextView targetBudgetTextView;
    private TextView dailySavingTextView;
    private RoundCornerProgressBar progressBar;

    private int _id;
    OnItemClickListener mItemClickListener;

    public TargetBudgetItemViewHolder(View itemView, int viewType, OnItemClickListener mItemClickListener){
        super(itemView);

        this.mItemClickListener = mItemClickListener;
        itemView.setOnClickListener(this);

        this.viewType = viewType;
        if(viewType == BudgetRecyclerViewAdapter.CHILD_FIXED_BUDGET){
            nameTextView = (TextView) itemView.findViewById(R.id.item_tab_budget_fixed_name);
            currentBudgetTextView = (TextView) itemView.findViewById(R.id.item_tab_budget_fixed_budget);
        }else{
            nameTextView = (TextView) itemView.findViewById(R.id.target_budget_name);
            currentBudgetTextView = (TextView) itemView.findViewById(R.id.target_budget_current_budget);
            targetBudgetTextView = (TextView) itemView.findViewById(R.id.target_budget_target_budget);
            dailySavingTextView = (TextView) itemView.findViewById(R.id.target_budget_daily_saving);
            progressBar = (RoundCornerProgressBar) itemView.findViewById(R.id.target_budget_progressBar);
        }
    }

    public void bind(TargetBudgetItem targetBudgetItem, int childPosition){
        this._id = targetBudgetItem.getId();

        if(viewType == BudgetRecyclerViewAdapter.CHILD_FIXED_BUDGET){
            String formattedFixedBudget = String.format(Locale.US, "%,d", targetBudgetItem.getCurrentBudget())+ "원";

            // set textView
            nameTextView.setText(targetBudgetItem.getName());
            currentBudgetTextView.setText(formattedFixedBudget);
        }else {
            int currentDate = StandardFormat.getCurrentDate();
            int currentBudget = targetBudgetItem.getCurrentBudget();
            int targetBudget = targetBudgetItem.getTargetBudget();
            int dueDate = targetBudgetItem.getDueDate();
            int dailySaving = ((targetBudget - currentBudget) / StandardFormat.getDeltaDate(currentDate, dueDate));

            // from style - currentBudget & targetBudget
            String formattedCurrentBudget = String.format(Locale.US, "%,d", currentBudget)+ "원";
            String formattedTargetBudget = String.format(Locale.US, "%,d", targetBudget)+ "원";

            String dailySavingString;
            if (currentBudget >= targetBudget) {
                dailySavingString = "목표를 달성하였습니다.";
            } else if (currentDate > dueDate) {
                dailySavingString = "기한이 지나였습니다.";
            } else {
                dailySavingString = "매일 " + String.valueOf(dailySaving) + "원";
            }

            // set textView
            nameTextView.setText(targetBudgetItem.getName());
            currentBudgetTextView.setText(formattedCurrentBudget);
            targetBudgetTextView.setText(formattedTargetBudget);
            dailySavingTextView.setText(dailySavingString);
            progressBar.setMax(targetBudget);
            progressBar.setProgress(currentBudget);

            // set color
            int color = getProgressBarColor(childPosition);
            currentBudgetTextView.setTextColor(color);
            progressBar.setProgressColor(color);
        }
    }

    @Override
    public void onClick(View view) {
        if(mItemClickListener != null){
            mItemClickListener.onItemClicked(view, this);
        }
    }

    public int getId(){
        return _id;
    }

    public int getViewType(){
        return viewType;
    }

    private int getProgressBarColor(int childPosition){
        int color;

        switch (childPosition){
            case 0:
                color = Color.parseColor("#2ECC71");
                break;
            case 1:
                color = Color.parseColor("#3498DB");
                break;
            case 2:
                color = Color.parseColor("#9B59B6");
                break;
            case 3:
                color = Color.parseColor("#F1C40f");
                break;
            case 4:
                color = Color.parseColor("#e67e22");
                break;
            case 5:
                color = Color.parseColor("#c0392b");
                break;
            default:
                color = Color.parseColor("#7f8c8d");
                break;
        }
        return color;
    }

}

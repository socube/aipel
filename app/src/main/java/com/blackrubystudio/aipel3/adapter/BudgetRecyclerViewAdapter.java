package com.blackrubystudio.aipel3.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.blackrubystudio.aipel3.R;
import com.blackrubystudio.aipel3.model.BudgetHeader;
import com.blackrubystudio.aipel3.util.OnItemClickListener;
import com.blackrubystudio.aipel3.viewholder.BudgetHeaderViewHolder;
import com.blackrubystudio.aipel3.model.TargetBudgetItem;
import com.blackrubystudio.aipel3.viewholder.TargetBudgetItemViewHolder;

import java.util.ArrayList;

/**
 * Created by jaewoo on 2017. 1. 7..
 */

public class BudgetRecyclerViewAdapter
        extends ExpandableRecyclerAdapter<BudgetHeader, TargetBudgetItem, BudgetHeaderViewHolder, TargetBudgetItemViewHolder> {

    public static final int CHILD_FIXED_BUDGET = 0;
    private static final int CHILD_TARGET_BUDGET = 1;
    private int viewType = -1;
    OnItemClickListener mItemClickListener;

    private LayoutInflater mInflater;

    public BudgetRecyclerViewAdapter(Context context, ArrayList<BudgetHeader> budgetHeaders){
        super(budgetHeaders);
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BudgetHeaderViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.item_tab_expense_head, parentViewGroup, false);
        this.viewType++;
        return new BudgetHeaderViewHolder(view);
    }

    @NonNull
    @Override
    public TargetBudgetItemViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View view;

        switch (this.viewType){
            default:
            case CHILD_FIXED_BUDGET:
                view = mInflater.inflate(R.layout.item_tab_budget_fixed_budget, childViewGroup, false);
                break;
            case CHILD_TARGET_BUDGET:
                view = mInflater.inflate(R.layout.item_tab_budget_target_budget, childViewGroup, false);
                break;
        }
        return new TargetBudgetItemViewHolder(view, this.viewType, mItemClickListener);
    }

    @Override
    public void onBindParentViewHolder(@NonNull BudgetHeaderViewHolder parentViewHolder, int parentPosition, @NonNull BudgetHeader parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull TargetBudgetItemViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull TargetBudgetItem child) {
        childViewHolder.bind(child, childPosition);
    }

    public void SetOnItemClickListener(OnItemClickListener itemClickListener){
        this.mItemClickListener = itemClickListener;
    }
}

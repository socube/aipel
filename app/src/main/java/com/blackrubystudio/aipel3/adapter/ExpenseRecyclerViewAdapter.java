package com.blackrubystudio.aipel3.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.blackrubystudio.aipel3.R;
import com.blackrubystudio.aipel3.model.AccountingItem;
import com.blackrubystudio.aipel3.util.OnItemClickListener;
import com.blackrubystudio.aipel3.viewholder.AccountingItemViewHolder;
import com.blackrubystudio.aipel3.model.ExpenseItem;
import com.blackrubystudio.aipel3.viewholder.ExpenseItemViewHolder;

import java.util.ArrayList;

/**
 * Created by jaewoo on 2017. 1. 5..
 */

public class ExpenseRecyclerViewAdapter
        extends ExpandableRecyclerAdapter<ExpenseItem, AccountingItem, ExpenseItemViewHolder, AccountingItemViewHolder>{

    private LayoutInflater mInflater;
    private Context mContext;
    OnItemClickListener mItemClickListener;

    public ExpenseRecyclerViewAdapter(Context context, ArrayList<ExpenseItem> mDateList){
        super(mDateList);
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public ExpenseItemViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.item_tab_expense_head, parentViewGroup, false);
        return new ExpenseItemViewHolder(view);
    }

    @NonNull
    @Override
    public AccountingItemViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.item_tab_expense, childViewGroup, false);
        return new AccountingItemViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindParentViewHolder(@NonNull ExpenseItemViewHolder parentViewHolder, int parentPosition, @NonNull ExpenseItem parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull AccountingItemViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull AccountingItem child) {
        childViewHolder.bind(child);
    }

    public void SetOnItemClickListener(OnItemClickListener itemClickListener){
        this.mItemClickListener = itemClickListener;
    }
}

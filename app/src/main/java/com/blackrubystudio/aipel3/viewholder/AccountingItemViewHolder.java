package com.blackrubystudio.aipel3.viewholder;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.blackrubystudio.aipel3.R;
import com.blackrubystudio.aipel3.model.AccountingItem;
import com.blackrubystudio.aipel3.util.OnItemClickListener;
import com.blackrubystudio.aipel3.util.StandardFormat;

import java.util.Locale;

/**
 * Created by jaewoo on 2017. 1. 5..
 */

public class AccountingItemViewHolder extends ChildViewHolder
        implements View.OnClickListener{

    private TextView placeTextView;
    private TextView categoryTextView;
    private TextView moneyTextView;
    private ImageView imageView;
    private int _id;
    OnItemClickListener mItemClickListener;


    public AccountingItemViewHolder(View itemView, OnItemClickListener mItemClickListener){
        super(itemView);

        this.mItemClickListener = mItemClickListener;
        itemView.setOnClickListener(this);

        placeTextView = (TextView) itemView.findViewById(R.id.item_tab_activity_place);
        categoryTextView = (TextView) itemView.findViewById(R.id.item_tab_activity_category);
        moneyTextView = (TextView) itemView.findViewById(R.id.item_tab_activity_money);
        imageView = (ImageView) itemView.findViewById(R.id.item_tab_activity_image);
    }

    public void bind(AccountingItem accountingItem){
        this._id = accountingItem.getId();

        placeTextView.setText(accountingItem.getPlace());
        categoryTextView.setText(String.valueOf(accountingItem.getIndex()));

        // set expense & category image
        int price = accountingItem.getMoney();
        String formattedPrice = String.format(Locale.US, "%,d", price)+ "원";
        int categoryNum = accountingItem.getIntegerIndex();
        if(price >0){
            moneyTextView.setTextColor(Color.parseColor("#008F80"));
            formattedPrice = "+" + formattedPrice;
            imageView.setImageResource(R.drawable.ic_earn);
        }else{
            moneyTextView.setTextColor(Color.parseColor("#F27166"));
            imageView.setImageResource(R.drawable.ic_expense_0 + categoryNum);
        }
        moneyTextView.setText(formattedPrice);
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
}

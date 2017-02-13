package com.blackrubystudio.aipel3.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackrubystudio.aipel3.R;
import com.blackrubystudio.aipel3.RegisterExpense;
import com.blackrubystudio.aipel3.TabActivity;
import com.blackrubystudio.aipel3.adapter.ExpenseRecyclerViewAdapter;
import com.blackrubystudio.aipel3.db.DatabaseHelper;
import com.blackrubystudio.aipel3.model.AccountingItem;
import com.blackrubystudio.aipel3.model.ExpenseItem;
import com.blackrubystudio.aipel3.util.DialogSafeToSpendUtils;
import com.blackrubystudio.aipel3.util.OnItemClickListener;
import com.blackrubystudio.aipel3.util.StandardFormat;
import com.blackrubystudio.aipel3.viewholder.AccountingItemViewHolder;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jaewoo on 2016. 12. 29..
 */

public class ExpenseFragment extends Fragment{

    private static final String PREFERENCE = "preference";

    RecyclerView recyclerView;
    ExpenseRecyclerViewAdapter adapter;
    DatabaseHelper dbHelper;
    DialogSafeToSpendUtils dialogSafeToSpendUtils;
    LinearLayout safeToSpendLinearLayout;
    TextView safeToSpendTextView;

    private ArrayList<ExpenseItem> expenseItems;

    public ExpenseFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Initialize database
        if(dbHelper == null){
            dbHelper = new DatabaseHelper(getContext());
        }
        //dbHelper.delete(DatabaseHelper.Table.EXPENSE);

        // Initialize view
        View view = inflater.inflate(R.layout.fragment_tab_expense, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_tab_expense_recyclerView);
        safeToSpendLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_tab_expense_linearLayout);
        safeToSpendTextView = (TextView) view.findViewById(R.id.fragment_tab_expense_safe_to_spend);

        // Initialize recyclerView and adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initLinearLayout();

        // Initialize dialog
        dialogSafeToSpendUtils = new DialogSafeToSpendUtils(this, true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED");
        getActivity().registerReceiver(broadcastReceiver, intentFilter);

        refreshItems();
        recyclerView.smoothScrollToPosition(0);
        super.onResume();
        ((TabActivity) getActivity()).fragmentCalled();
    }

    public void refreshSafeToSpend(){
        SharedPreferences pref = this.getActivity().getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        int safeToSpend = pref.getInt("safe_to_spend", 0);
        int currentMonthExpense = pref.getInt("current_month_expense", 0);
        String formattedSafeToSpend = StandardFormat.onCurrencyFormat(safeToSpend+currentMonthExpense)+ "원";
        safeToSpendTextView.setText(formattedSafeToSpend);
    }

    ArrayList<ExpenseItem> getExpenseItems() {
        ArrayList<Object> objectArrayList = dbHelper.select(DatabaseHelper.Table.EXPENSE);
        ArrayList<AccountingItem> accountingItems = new ArrayList<>();

        Log.d("expense", "objectArrayList size: "+objectArrayList.size());

        // Get section dates
        ArrayList<ExpenseItem> expenseItemArrayList = new ArrayList<>();

        for (Object object : objectArrayList) {
            accountingItems.add((AccountingItem) object);
        }

        if (!accountingItems.isEmpty()) {
            // Get section indices
            ArrayList<Integer> sectionIndices = new ArrayList<>();
            Integer lastFirstDate = accountingItems.get(0).getDate();
            sectionIndices.add(0);

            int size = accountingItems.size();

            for (int i = 0; i < size; i++) {
                AccountingItem item = accountingItems.get(i);
                if (i != 0 && item.getDate() != lastFirstDate) {
                    lastFirstDate = item.getDate();
                    sectionIndices.add(i);
                }
                // set category integer index
                if(item.getIntegerIndex() == -1){
                    int categoryNum;
                    if(item.getMoney() < 0){
                        categoryNum = StandardFormat.getCategoryNum(item.getIndex(), true);
                    }else{
                        categoryNum = StandardFormat.getCategoryNum(item.getIndex(), false);
                    }
                    // update data
                    item.setIntegerIndex(categoryNum);
                    dbHelper.update(DatabaseHelper.Table.EXPENSE, item);
                }
            }

            sectionIndices.add(size);

            int sectionLength = sectionIndices.size() - 1;
            int startOfMonth = StandardFormat.getCurrentDate()/100 * 100;
            int currentMonthExpense = 0;
            for (int i = 0; i < sectionLength; i++) {
                // Get child view - AccountingItem
                int startItemNum = sectionIndices.get(i);
                int endItemNum = sectionIndices.get(i + 1);

                ArrayList<AccountingItem> itemArrayList = new ArrayList<>();
                itemArrayList.addAll(accountingItems.subList(startItemNum, endItemNum));

                // Get head view - date
                int date = accountingItems.get(startItemNum).getDate();
                if(date > startOfMonth){
                    for(AccountingItem item: itemArrayList){
                        currentMonthExpense += item.getMoney();
                    }
                }

                expenseItemArrayList.add(new ExpenseItem(date, itemArrayList));
            }
            SharedPreferences pref = this.getActivity().getSharedPreferences(PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("current_month_expense", currentMonthExpense);
            editor.apply();
        }
        Log.d("expense", "expenseItem size: "+expenseItemArrayList.size());
        return expenseItemArrayList;
    }

    private void initLinearLayout() {
        safeToSpendLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSafeToSpendUtils.showDialog();
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int lastParentCount = adapter.getParentList().size();

            expenseItems.clear();
            expenseItems.addAll(getExpenseItems());

            int parentCount = adapter.getParentList().size();

            int parentIncrement = parentCount - lastParentCount;
            if (parentIncrement > 0) {
                adapter.notifyParentInserted(0);
            } else {
                adapter.notifyChildInserted(0, adapter.getParentList().get(0).getChildList().size() - 1);
            }
        }
    };

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    public void refreshItems(){
        expenseItems= new ArrayList<>();
        expenseItems.addAll(getExpenseItems());
        adapter = new ExpenseRecyclerViewAdapter(getContext(), expenseItems);
        adapter.SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, Object object) {
                AccountingItemViewHolder item = (AccountingItemViewHolder) object;

                Intent intent = new Intent(getActivity(), RegisterExpense.class);
                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}

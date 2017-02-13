package com.blackrubystudio.aipel3.Fragment;

import android.content.Intent;
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
import com.blackrubystudio.aipel3.RegisterBudget;
import com.blackrubystudio.aipel3.TabActivity;
import com.blackrubystudio.aipel3.adapter.BudgetRecyclerViewAdapter;
import com.blackrubystudio.aipel3.db.DatabaseHelper;
import com.blackrubystudio.aipel3.model.BudgetHeader;
import com.blackrubystudio.aipel3.model.TargetBudgetItem;
import com.blackrubystudio.aipel3.util.DialogSafeToSpendUtils;
import com.blackrubystudio.aipel3.util.OnItemClickListener;
import com.blackrubystudio.aipel3.util.StandardFormat;
import com.blackrubystudio.aipel3.viewholder.TargetBudgetItemViewHolder;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jaewoo on 2016. 12. 29..
 */

public class BudgetFragment extends Fragment {

    private static final String PREFERENCE = "preference";

    RecyclerView recyclerView;
    BudgetRecyclerViewAdapter adapter;
    DatabaseHelper dbHelper;
    DialogSafeToSpendUtils dialogSafeToSpendUtils;
    LinearLayout safeToSpendLinearLayout;
    TextView safeToSpendTextView;
    int safeToSpend;

    public BudgetFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Initialize database
        if(dbHelper == null){
            dbHelper = new DatabaseHelper(getContext());
        }
        //InsertData();

        // Initialize view
        View view = inflater.inflate(R.layout.fragment_tab_budget, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_tab_budget_recyclerView);
        safeToSpendLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_tab_budget_linearLayout);
        safeToSpendTextView = (TextView) view.findViewById(R.id.fragment_tab_budget_safe_to_spend);

        // Initialize recyclerView, adapter and linearLayout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initLinearLayout();

        // Initialize dialog
        dialogSafeToSpendUtils = new DialogSafeToSpendUtils(this, false);

        return view;
    }

    @Override
    public void onResume(){
        refreshItems();
        recyclerView.smoothScrollToPosition(0);
        String formattedSafeToSpend = StandardFormat.onCurrencyFormat(safeToSpend)+ "원";
        safeToSpendTextView.setText(formattedSafeToSpend);
        super.onResume();
        ((TabActivity)getActivity()).fragmentCalled();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private ArrayList<BudgetHeader> getBudgetHeaders(){
        ArrayList<Object> objectArrayList = dbHelper.select(DatabaseHelper.Table.BUDGET);
        ArrayList<BudgetHeader> budgetHeaders = new ArrayList<>();

        ArrayList<TargetBudgetItem> fixedBudgetLists = new ArrayList<>();
        ArrayList<TargetBudgetItem> targetBudgetLists = new ArrayList<>();

        // calculate for safe to spend
        int fixed_budget = 0;
        int target_budget = 0;

        // get currentMonth and renewalMonth
        int currentDate = StandardFormat.getCurrentDate();
        int currentMonth = currentDate / 100; //format: yyyyMM
        SharedPreferences pref = this.getActivity().getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        int renewalDate = pref.getInt("renewal_date", StandardFormat.getCurrentDate()); //format: yyyyMMdd
        int renewalMonth = renewalDate / 100;

        // separate fixed budget and target budget
        for (Object object : objectArrayList){
            TargetBudgetItem item = (TargetBudgetItem) object;

            if(item.isGoal() < 1){
                fixed_budget += item.getCurrentBudget();
                fixedBudgetLists.add(item);
            }else{
                int currentBudget = item.getCurrentBudget();
                int deltaDays = StandardFormat.getDeltaDate(renewalDate, item.getDueDate());
                int dailySaving;
                if(deltaDays > 0){
                    dailySaving = (item.getTargetBudget() - item.getCurrentBudget()) / deltaDays;
                }else{
                    dailySaving = 0;
                }
                int passedDays = StandardFormat.getDeltaDate(renewalDate, currentDate);
                item.setCurrentBudget(currentBudget + dailySaving * passedDays);

                // less than one month
                if(currentMonth == renewalMonth){
                    target_budget += item.getMonthlyBudget();
                }else if (currentMonth < renewalMonth){
                    // more than one month
                    int monthlySavingBudget = StandardFormat.getMonthlySavingBudget(item.getDueDate(), item.getCurrentBudget(), item.getTargetBudget());
                    target_budget += monthlySavingBudget;

                    item.setMonthlyBudget(monthlySavingBudget);
                }
                dbHelper.update(DatabaseHelper.Table.BUDGET, item);
                targetBudgetLists.add(item);
            }
        }
        // update data
        safeToSpend = pref.getInt("total_budget", 500000) - fixed_budget - target_budget;
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("renewal_date", currentDate);
        editor.putInt("fixed_budget", fixed_budget);
        editor.putInt("target_budget", target_budget);
        editor.putInt("safe_to_spend", safeToSpend);
        editor.apply();

        BudgetHeader fixedBudget = new BudgetHeader("고정 지출", fixedBudgetLists);
        BudgetHeader targetBudget = new BudgetHeader("목표 관리", targetBudgetLists);

        budgetHeaders.add(fixedBudget);
        budgetHeaders.add(targetBudget);

        return budgetHeaders;
    }

    private void initLinearLayout() {
        safeToSpendLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSafeToSpendUtils.showDialog();
            }
        });
    }

    public void refreshItems(){
        adapter = new BudgetRecyclerViewAdapter(getContext(), getBudgetHeaders());
        adapter.SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, Object object) {
                TargetBudgetItemViewHolder item = (TargetBudgetItemViewHolder) object;
                Intent intent = new Intent(getActivity(), RegisterBudget.class);

                if(item.getViewType() == BudgetRecyclerViewAdapter.CHILD_FIXED_BUDGET) {
                    intent.putExtra("isFixed", true);
                }

                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}

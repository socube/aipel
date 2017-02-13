package com.blackrubystudio.aipel3;

import com.blackrubystudio.aipel3.util.TabViewPagerChangeListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.blackrubystudio.aipel3.adapter.TabAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaewoo on 2016. 12. 29..
 */

public class TabActivity extends AppCompatActivity {

    @BindView(R.id.tab_toolbar) Toolbar toolbar;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.tab_viewpager) ViewPager viewPager;
    @BindView(R.id.tab_button_fixed) FloatingActionButton actionButtonFixedBudget;
    @BindView(R.id.tab_button_target) FloatingActionButton actionButtonTargetBudget;
    @BindView(R.id.tab_button_expense) FloatingActionButton actionButtonExpense;
    @BindView(R.id.tab_button_earn) FloatingActionButton actionButtonEarn;
    @BindView(R.id.tab_button_menu) FloatingActionsMenu actionsMenu;
    @BindView(R.id.tab_fade_screen) LinearLayout linearLayout;
    @BindView(R.id.tab_swipe_refresh_layout) SwipeRefreshLayout refreshLayout;

    TabAdapter tabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        ButterKnife.bind(this);

        // initialize toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // initialize viewPager & tabLayout
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.addTab(tabLayout.newTab().setText("사용내역"));
        tabLayout.addTab(tabLayout.newTab().setText("목표관리"));
        //tabLayout.addTab(tabLayout.newTab().setText("소비패턴"));
        viewPager.addOnPageChangeListener(new TabViewPagerChangeListener(tabLayout,
                new FloatingActionButton[]{actionButtonExpense, actionButtonEarn,
                        actionButtonFixedBudget, actionButtonTargetBudget}));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        // initialize floating action button
        initializeActionButtons();

        // initialize swipeRefreshLayout
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(actionsMenu.isExpanded()){
            actionsMenu.toggle();
        }
        refreshLayout.setRefreshing(true);
    }

    public void fragmentCalled(){
        tabAdapter.refreshSafeToSpend();
        refreshLayout.setRefreshing(false);
    }

    /**
        onClick
     */
    private void initializeActionButtons(){
        actionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                linearLayout.setVisibility(View.GONE);
            }
        });

        actionButtonExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TabActivity.this, RegisterExpense.class);
                intent.putExtra("isExpense", true);
                actionsMenu.toggle();
                startActivity(intent);
            }
        });
        actionButtonEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TabActivity.this, RegisterExpense.class);
                intent.putExtra("isExpense", false);
                actionsMenu.toggle();
                startActivity(intent);
            }
        });
        actionButtonFixedBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TabActivity.this, RegisterBudget.class);
                intent.putExtra("isFixed", true);
                actionsMenu.toggle();
                startActivity(intent);
            }
        });
        actionButtonTargetBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TabActivity.this, RegisterBudget.class);
                intent.putExtra("isFixed", false);
                actionsMenu.toggle();
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.tab_fade_screen)
    public void onScreenClicked(){
        actionsMenu.toggle();
    }

    /**
     * refresh layout
     */
    void refreshItems(){
        tabAdapter.refreshItem(tabLayout.getSelectedTabPosition());
        onItemsLoadComplete();
    }

    void onItemsLoadComplete(){
        refreshLayout.setRefreshing(false);
    }
}

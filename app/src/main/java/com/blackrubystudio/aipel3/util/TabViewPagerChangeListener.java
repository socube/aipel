package com.blackrubystudio.aipel3.util;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * Created by jaewoo on 2017. 1. 15..
 */

public class TabViewPagerChangeListener implements ViewPager.OnPageChangeListener {

    private TabLayout.TabLayoutOnPageChangeListener changeListener;
    private FloatingActionButton[] actionButtons;

    public TabViewPagerChangeListener(TabLayout tabLayout, FloatingActionButton[] actionButtons){
        changeListener = new TabLayout.TabLayoutOnPageChangeListener(tabLayout);
        this.actionButtons = actionButtons;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        changeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        orderActionButton(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        changeListener.onPageScrollStateChanged(state);
    }

    @Override
    public void onPageSelected(int position) {
        changeListener.onPageSelected(position);
        orderActionButton(position);
    }

    private void orderActionButton(int position){
        switch (position){
            case 0:
                actionButtons[0].setVisibility(View.VISIBLE);
                actionButtons[1].setVisibility(View.VISIBLE);
                actionButtons[2].setVisibility(View.GONE);
                actionButtons[3].setVisibility(View.GONE);
                break;
            case 1:
                actionButtons[0].setVisibility(View.GONE);
                actionButtons[1].setVisibility(View.GONE);
                actionButtons[2].setVisibility(View.VISIBLE);
                actionButtons[3].setVisibility(View.VISIBLE);
                break;
        }
    }
}

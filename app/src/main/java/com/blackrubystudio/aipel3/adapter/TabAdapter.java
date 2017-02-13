package com.blackrubystudio.aipel3.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.blackrubystudio.aipel3.Fragment.ExpenseFragment;
import com.blackrubystudio.aipel3.Fragment.BudgetFragment;

/**
 * Created by jaewoo on 2016. 12. 29..
 */

public class TabAdapter extends FragmentPagerAdapter {

    private final int PAGE_NUM = 2;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public TabAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return new ExpenseFragment();
            case 1:
                return new BudgetFragment();
        }
        return new ExpenseFragment();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount(){
        return PAGE_NUM;
    }

    public void refreshItem(int position){
        switch (position){
            case 0:
                ((ExpenseFragment)registeredFragments.get(0)).refreshItems();
            case 1:
                ((BudgetFragment)registeredFragments.get(1)).refreshItems();
        }
    }

    public void refreshSafeToSpend(){
        ((ExpenseFragment)registeredFragments.get(0)).refreshSafeToSpend();
    }
}

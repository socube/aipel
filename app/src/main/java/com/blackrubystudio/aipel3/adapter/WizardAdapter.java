package com.blackrubystudio.aipel3.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackrubystudio.aipel3.R;

/**
 * Created by jaewoo on 2016. 12. 29..
 */

public class WizardAdapter extends PagerAdapter {

    private final int TUTORIAL_NUM = 3;
    LayoutInflater inflater;

    public WizardAdapter(LayoutInflater inflater){
        this.inflater = inflater;;
    }

    @Override
    public int getCount() {
        return TUTORIAL_NUM;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_wizard, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.fragment_wizard_icon);
        TextView headTextView = (TextView) view.findViewById(R.id.fragment_wizard_head);
        TextView detailTextView = (TextView) view.findViewById(R.id.fragment_wizard_text);

        imageView.setImageResource(R.drawable.fragment_wizard_image01+position);
        headTextView.setText(R.string.head1+position);
        detailTextView.setText(R.string.description1+position);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

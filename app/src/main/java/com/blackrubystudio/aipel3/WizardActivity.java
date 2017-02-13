package com.blackrubystudio.aipel3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blackrubystudio.aipel3.adapter.WizardAdapter;
import com.blackrubystudio.aipel3.util.CircleAnimIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WizardActivity extends AppCompatActivity{

    @BindView(R.id.activity_wizard_viewPager) ViewPager viewPager;
    @BindView(R.id.activity_wizard_skip) TextView skipButton;
    @BindView(R.id.activity_wizard_next) ImageButton nextButton;
    @BindView(R.id.activity_wizard_circle) CircleAnimIndicator circleAnimIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);
        ButterKnife.bind(this);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Init ViewPager
        WizardAdapter adapter = new WizardAdapter(getLayoutInflater());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);

        // Init Indicator
        circleAnimIndicator.setItemMargin(15);
        circleAnimIndicator.setAnimDuration(300);
        circleAnimIndicator.createDotPanel(3, R.drawable.wizard_indicator_off, R.drawable.wizard_indicator_on);
    }

    /**
     * ViewPager 전환시 호출되는 메서드
     */
    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            circleAnimIndicator.selectDot(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @OnClick(R.id.activity_wizard_next)
    public void onNextClicked(){
        int position = viewPager.getCurrentItem();
        if(position <2) {
            viewPager.setCurrentItem(position + 1, true);
        }else{
            onSkipClicked();
        }
    }

    @OnClick(R.id.activity_wizard_skip)
    public void onSkipClicked(){
        Intent i = new Intent(this, LoginSelectionActivity.class);
        startActivity(i);
        finish();
    }

}

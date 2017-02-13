package com.blackrubystudio.aipel3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.blackrubystudio.aipel3.util.DialogSmsReadUtils;
import com.blackrubystudio.aipel3.util.StandardFormat;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaewoo on 2017. 1. 19..
 */

public class OptionActivity extends AppCompatActivity {

    @BindView(R.id.option_toolbar) Toolbar toolbar;
    @BindView(R.id.option_sms_date) TextView dateTextView;
    @BindView(R.id.option_email) TextView emailTextView;

    int startDate;
    DialogSmsReadUtils dialogSmsReadUtils;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_option);
        ButterKnife.bind(this);

        // initialize toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dateTextView.setText(getDateText());
        dialogSmsReadUtils = new DialogSmsReadUtils(OptionActivity.this, startDate);

        String emailString = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if(emailString == null){
            emailString = "클릭시 로그아웃 됩니다.";
        }
        emailTextView.setText(emailString);

        checkSMSPermissions();
    }

    private void checkSMSPermissions(){
        int permissionCheck = ContextCompat.checkSelfPermission(OptionActivity.this, android.Manifest.permission.READ_SMS);
        if(!(permissionCheck == PackageManager.PERMISSION_GRANTED)){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(OptionActivity.this, android.Manifest.permission.READ_SMS)) {
                ActivityCompat.requestPermissions(OptionActivity.this,
                        new String[]{android.Manifest.permission.READ_SMS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int responseCode, String[] permissions,
                                           int[] grantResults){
        if(responseCode == 1){
            for(int i =0; i <permissions.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(OptionActivity.this, permissions[i]+ "권한이 승인됨.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(OptionActivity.this, permissions[i]+ "권한이 승인되지 않음.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     *  OnClick
     */
    @OnClick(R.id.option_x)
    public void onBackIconClicked(){
        finish();
    }

    @OnClick(R.id.option_title)
    public void onTitleClicked(){
        finish();
    }

    @OnClick(R.id.option_sms)
    public void onSmsClicked(){
        dialogSmsReadUtils.showDialog();
    }

    @OnClick(R.id.option_id)
    public void onLogOutClicked(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginSelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.option_license)
    public void onLicenseClicked(){
        Toast.makeText(this, "오픈소스 라이선스를 준비중입니다.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.option_service)
    public void onServiceClicked(){
        Toast.makeText(this, "서비스 이용약관을 준비중입니다.", Toast.LENGTH_SHORT).show();
    }

    /**
     *  Method
     */
    private String getDateText(){
        int currentDate = StandardFormat.getCurrentDate();
        int year = currentDate / 10000;
        int month = (currentDate/100)%100;
        int day = currentDate % 100;

        if(month > 3){
            startDate = year * 10000 + (month-3) * 100 + day;
            Log.d("OptionActivity", "startDate: "+startDate);
            return String.format(Locale.US, "%d년 %d월 %d일 ~ %d년 %d월 %d일", year, month-3, day, year, month, day);
        }else{
            startDate = (year-1) * 10000 + (month+12-3) * 100 + day;
            Log.d("OptionActivity", "startDate: "+startDate);
            return String.format(Locale.US, "%d년 %d월 %d일 ~ %d년 %d월 %d일", year-1, month+12-3, day, year, month, day);
        }
    }
}

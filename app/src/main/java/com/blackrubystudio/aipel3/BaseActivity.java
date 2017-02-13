package com.blackrubystudio.aipel3;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jaewoo on 2017. 1. 11..
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog(){
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}

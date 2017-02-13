package com.blackrubystudio.aipel3.util;

import android.app.Dialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.blackrubystudio.aipel3.R;
import com.blackrubystudio.aipel3.api.CategoryAPI;
import com.blackrubystudio.aipel3.db.DatabaseHelper;
import com.blackrubystudio.aipel3.db.Models;
import com.blackrubystudio.aipel3.sms.SmsCategoryReceiver;
import com.blackrubystudio.aipel3.sms.SmsHelper;
import com.blackrubystudio.aipel3.sms.SmsObject;

import java.util.ArrayList;

/**
 * Created by jaewoo on 2017. 1. 19..
 */

public class DialogSmsReadUtils {

    private Dialog dialog;
    private RoundCornerProgressBar progressBar;
    private TextView percentTextView;
    private Context context;
    private int startDate;

    public DialogSmsReadUtils(Context context, int startDate){
        this.context = context;
        this.startDate = startDate;

        if(dialog == null){
            dialog = new Dialog(context, R.style.CustomDialogTheme);
        }

        dialog.setContentView(R.layout.dialog_read_sms);
        dialog.setCancelable(false);

        progressBar = (RoundCornerProgressBar) dialog.findViewById(R.id.dialog_sms_progressbar);
        //headTextView = (TextView) dialog.findViewById(R.id.dialog_sms_head);
        percentTextView = (TextView) dialog.findViewById(R.id.dialog_sms_percent);
    }

    public void showDialog(){
        new ReadSmsAsyncTask().execute();
    }

    private class ReadSmsAsyncTask extends AsyncTask<Void, Integer, Void> {
        int progress_status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            progress_status = 0;
            percentTextView.setText("0%");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // delete database
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            databaseHelper.drop(DatabaseHelper.Table.EXPENSE);
            databaseHelper.create(databaseHelper.getWritableDatabase(), Models.EXPENSE);

            progress_status += 10;
            publishProgress(progress_status);

            // get data
            SmsHelper smsHelper = new SmsHelper(context);
            ArrayList<SmsObject> smsObjects = smsHelper.getSmsFromInbox(startDate);
            Log.d("expense", "DialogSmsReadUtils smsObjects size: "+smsObjects.size());
            CategoryAPI categoryAPI = new CategoryAPI();

            progress_status += 10;
            publishProgress(progress_status);

            int maxNum = smsObjects.size();

            for(SmsObject item : smsObjects){
                categoryAPI.getCategory(item.getPlace()).enqueue(new SmsCategoryReceiver(item, context));
                Log.d("expense", "send message");
                progress_status += 80/maxNum;
                publishProgress(progress_status);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            String percent = String.valueOf(values[0])+"%";
            percentTextView.setText(percent);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(context, "문자를 모두 읽어왔습니다.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }
}

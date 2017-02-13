package com.blackrubystudio.aipel3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.blackrubystudio.aipel3.adapter.ChatAdapter;
import com.blackrubystudio.aipel3.sms.SmsService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaewoo on 2016. 12. 29..
 */

public class ChatActivity extends BaseActivity {

    @BindView(R.id.chat_toolbar) Toolbar toolbar;
    @BindView(R.id.chat_listMessages) RecyclerView mRecyclerView;

    private ChatAdapter chatAdapter;
    private DatabaseReference mMessageReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        checkSMSPermissions();

        if (!SmsService.isRunning(this)) {
            Intent i = new Intent(this, SmsService.class);
            startService(i);
        }

        // Initialize RecyclerView
        chatAdapter = new ChatAdapter();
        mRecyclerView.setAdapter(chatAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // New child entries
        mMessageReference = FirebaseDatabase.getInstance().getReference().child("message");
    }
/*
    @Override
    protected void onStart() {
        super.onStart();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                ArrayList<String> messages = message.getText();
                for(String item: messages){
                    chatAdapter.AddMessage(item);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mChildEventListener = childEventListener;
        mMessageReference.addChildEventListener(mChildEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mMessageReference != null){
            mMessageReference.removeEventListener(mChildEventListener);
        }
    }
*/
    @OnClick(R.id.chat_profile)
    public void onProfileClicked(){
        new activityAsyncTask(this).execute();
    }

    @OnClick(R.id.chat_title)
    public void onTitleClicked(){
        onProfileClicked();
    }

    private void checkSMSPermissions(){
        int permissionCheck = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECEIVE_SMS);
        if(!(permissionCheck == PackageManager.PERMISSION_GRANTED)){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this, Manifest.permission.RECEIVE_SMS)) {
                ActivityCompat.requestPermissions(ChatActivity.this,
                        new String[]{Manifest.permission.RECEIVE_SMS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int responseCode, String[] permissions,
                                           int[] grantResults){
        if(responseCode == 1){
            for(int i =0; i <permissions.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ChatActivity.this, permissions[i]+ "권한이 승인됨.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ChatActivity.this, permissions[i]+ "권한이 승인되지 않음.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_option) {
            Intent intent = new Intent(ChatActivity.this, OptionActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class activityAsyncTask extends AsyncTask<Void, Void, Void>{
        private Context context;

        private activityAsyncTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Intent intent = new Intent(context, TabActivity.class);
            context.startActivity(intent);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressDialog();
        }
    }
}

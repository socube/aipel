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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blackrubystudio.aipel3.adapter.ChatAdapter;
import com.blackrubystudio.aipel3.sms.SmsService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaewoo on 2016. 12. 29..
 */

public class ChatActivity extends BaseActivity {

    private static final int BUTTONS_PER_PAGE = 2;

    @BindView(R.id.chat_toolbar) Toolbar toolbar;
    @BindView(R.id.chat_listMessages) RecyclerView mRecyclerView;
    @BindView(R.id.chat_user_choice_first_row) LinearLayout user_choice_first_row;
    @BindView(R.id.chat_user_choice_second_row) LinearLayout user_choice_second_row;

    private ChatAdapter chatAdapter;
    private ArrayList<Button> buttonArrayList;

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

        // Initialize Chatting
        buttonArrayList = new ArrayList<>();
        getAIMessage();
    }

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

    // TODO: get AIPEL message from server
    private void getAIMessage(){
        //
        chatAdapter.AddMessage("역시 부지런한 당신, 아침부터 저를 찾아주시다니 정말 고마워요! \n" +
                "따뜻한 모닝커피 한 잔 어때요?ღ˘‿˘ற꒱", ChatAdapter.VIEW_TYPE_AIPEL);
        chatAdapter.AddMessage("좋아요", ChatAdapter.VIEW_TYPE_USER);
        chatAdapter.AddMessage("[사용자이름]님!  함께 진한 커피의 향에 빠져 봅시다!!\n" +
                "그 전에.......... 우리........날씨도 좋은데 오늘의 안전가용금액부터 살펴볼까요?", ChatAdapter.VIEW_TYPE_AIPEL);
        //
        chatAdapter.notifyDataSetChanged();
        getUserChoice();
    }

    // TODO: get user choice from server
    private void getUserChoice(){
        AddUserChoiceButton("좋아요", 0);
        AddUserChoiceButton("괜찮아요", 1);
        AddUserChoiceButton("2번째 줄 테스트 용", 2);
    }

    private void ChoiceButtonClicked(String message){
        chatAdapter.AddMessage(message, ChatAdapter.VIEW_TYPE_USER);

        for(int i=0; i<buttonArrayList.size(); i++){
            if(i<BUTTONS_PER_PAGE){
                user_choice_first_row.removeView(buttonArrayList.get(i));
            }else{
                user_choice_second_row.removeView(buttonArrayList.get(i));
            }
        }
        buttonArrayList.clear();

        // TODO: send choice to server and get next message
    }

    private void AddUserChoiceButton(final String message, int button_num){
        Button button = new Button(this);
        buttonArrayList.add(button);

        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(10, 0, 10, 0);
        button.setLayoutParams(params);
        button.setText(message);
        button.setBackgroundResource(R.drawable.user_message);

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                ChoiceButtonClicked(message);
            }
        });

        if(button_num < BUTTONS_PER_PAGE){
            user_choice_first_row.addView(button);
        }else{
            user_choice_second_row.addView(button);
        }
    }
}

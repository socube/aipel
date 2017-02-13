package com.blackrubystudio.aipel3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaewoo on 2016. 12. 29..
 */

public class LoginWithEmailActivity extends BaseActivity{

    private static final String PREFERENCE = "preference";
    private final String TAG = getClass().getName();
    private boolean isSignUp = true; // true - SignUp, false - SignIn

    private FirebaseAuth mAuth;

    @BindView(R.id.login_with_email) LinearLayout loginButton;
    @BindView(R.id.login_with_email_text) TextView loginButtonText;
    @BindView(R.id.login_email) EditText emailField;
    @BindView(R.id.login_password) EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);
        ButterKnife.bind(this);

        Intent i = getIntent();
        isSignUp = i.getBooleanExtra("isSignUp", true);

        mAuth = FirebaseAuth.getInstance();

        SwitchPage();
    }

    private void SwitchPage(){
        if(isSignUp){
            isSignUp = false;
            loginButtonText.setText(getResources().getString(R.string.sign_in_with_email));
        }else{
            isSignUp = true;
            loginButtonText.setText(getResources().getString(R.string.sign_up_with_email));
        }
    }

    /*
        OnClick
     */
    @OnClick(R.id.login_with_email)
    public void onLogInClicked(){
        if(isSignUp){
            signUp();
        }else{
            signIn();
        }
    }

    private void signUp(){
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            checkHasInfo();
                        } else {
                            Toast.makeText(LoginWithEmailActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            checkHasInfo();
                        } else {
                            Toast.makeText(LoginWithEmailActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*
        E-mail
     */
    private boolean validateForm(){
        boolean result = true;

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (email.isEmpty()) {
            emailField.setError("required");
            result = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("enter a valid email address");
            result = false;
        } else {
            emailField.setError(null);
        }

        if (password.isEmpty()){
            passwordField.setError("required");
            result = false;
        } else if(password.length() < 4 || password.length() > 15) {
            passwordField.setError("between 4 and 15 alphanumeric characters");
            result = false;
        } else {
            passwordField.setError(null);
        }

        return result;
    }

    /*
        Login Auth
     */
    private void checkHasInfo(){
        SharedPreferences pref = this.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        boolean hasInfo = pref.getBoolean("hasInfo", false);

        Intent intent;

        if(hasInfo) {
            intent = new Intent(LoginWithEmailActivity.this, ChatActivity.class);
        }else{
            intent = new Intent(LoginWithEmailActivity.this, SurveyActivity.class);
        }

        hideProgressDialog();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

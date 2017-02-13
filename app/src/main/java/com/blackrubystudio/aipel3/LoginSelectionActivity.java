package com.blackrubystudio.aipel3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class LoginSelectionActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener{

    private static final String PREFERENCE = "preference";
    private final String TAG = getClass().getName();
    private static final int RC_SIGN_IN = 9001;
    private boolean isSignUp = true; // true - SignUp, false - SignIn

    @BindView(R.id.login_selection_email_text) TextView emailTextView;
    @BindView(R.id.login_selection_google_text) TextView googleTextView;
    @BindView(R.id.login_selection_des1) TextView introTextView1;
    @BindView(R.id.login_selection_des2) TextView introTextView2;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_selection);
        ButterKnife.bind(this);

        Intent i = getIntent();
        isSignUp = i.getBooleanExtra("isSignUp", true);

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(LoginSelectionActivity.this.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SwitchPage();
    }

    private void SwitchPage(){
        if(isSignUp){
            isSignUp = false;
            emailTextView.setText(R.string.sign_up_with_email);
            googleTextView.setText(R.string.sign_up_with_google);
            introTextView1.setText(R.string.have_id);
            introTextView2.setText(R.string.go_to_login);
        }else{
            isSignUp = true;
            emailTextView.setText(R.string.sign_in_with_email);
            googleTextView.setText(R.string.sign_in_with_google);
            introTextView1.setText(R.string.no_id);
            introTextView2.setText(R.string.go_to_signUp);
        }
    }

    /*
        OnClick
     */
    @OnClick(R.id.login_selection_google)
    public void googleSignInClicked(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.login_selection_email)
    public void emailSignInClicked(){
        Intent intent = new Intent(LoginSelectionActivity.this, LoginWithEmailActivity.class);
        intent.putExtra("isSignUp", isSignUp);
        startActivity(intent);
    }

    @OnClick(R.id.login_selection_go_to)
    public void onTextClicked(){
        Intent intent = new Intent(LoginSelectionActivity.this, LoginSelectionActivity.class);
        intent.putExtra("isSignUp", isSignUp);
        startActivity(intent);
        finish();
        Log.d(TAG, "login layout clicked");
    }

    // google login
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG,"Google Sign In failed "+result.getStatus());
                Toast.makeText(LoginSelectionActivity.this, "Google Sign In failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginSelectionActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }else if (task.isSuccessful()){
                            checkHasInfo();
                        }
                    }
                });
    }

    private void checkHasInfo(){
        SharedPreferences pref = this.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        boolean hasInfo = pref.getBoolean("hasInfo", false);

        Intent intent;
        if(hasInfo) {
            intent = new Intent(LoginSelectionActivity.this, ChatActivity.class);
        }else{
            intent = new Intent(LoginSelectionActivity.this, SurveyActivity.class);
        }

        hideProgressDialog();
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}

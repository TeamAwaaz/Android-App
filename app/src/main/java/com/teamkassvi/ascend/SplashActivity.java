package com.teamkassvi.ascend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import static com.teamkassvi.ascend.LoginActivity.EMAIL_ID;
import static com.teamkassvi.ascend.LoginActivity.USERNAME;
import static com.teamkassvi.ascend.LoginActivity.USER_IMAGE;

public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_DISPLAY_LENGTH = 1000;
    LoginDataBaseAdapter loginDataBaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_splash);
//        linearLayout.getBackground().setAlpha(30);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLoggedIn();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void checkLoggedIn() {

        Intent mainIntent;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userName = settings.getString(USERNAME,null);
        if(userName!=null){
            automateLogin(userName);
        }
        else{
            mainIntent = new Intent(SplashActivity.this, LoginActivity.class);

            SplashActivity.this.startActivity(mainIntent);
            SplashActivity.this.finish();
        }
    }

    private void automateLogin(String userName) {
        Intent intent = new Intent(this, GetStartedActivity.class);
        String emailId = loginDataBaseAdapter.getEmailId(userName);
        String userImageUri = loginDataBaseAdapter.getUri(userName);
        Log.d("TAGimage: ", userImageUri+"");
        intent.putExtra(USERNAME, userName);
        intent.putExtra(EMAIL_ID, emailId);
        intent.putExtra(USER_IMAGE,userImageUri);
        startActivity(intent);
    }
}

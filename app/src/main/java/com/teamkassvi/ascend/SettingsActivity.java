package com.teamkassvi.ascend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static com.teamkassvi.ascend.LoginActivity.EMAIL_ID;
import static com.teamkassvi.ascend.LoginActivity.USERNAME;
import static com.teamkassvi.ascend.LoginActivity.USER_IMAGE;

public class SettingsActivity extends AppCompatActivity {

    EditText etValence, etArousal;
    Button saveOffsets;
    public int valenceOffset = -15, arousalOffset = -27;
    LoginDataBaseAdapter loginDataBaseAdapter;
    SharedPreferences preferences;
    public static final String VALENCE_OFFSET = "valence_offset";
    public static final String AROUSAL_OFFSET = "arousal_offset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        Toast.makeText(this, "Set Valence offset to ", Toast.LENGTH_SHORT).show();
        etValence = (EditText) findViewById(R.id.tv_valence);
        etArousal = (EditText) findViewById(R.id.tv_arousal);
        saveOffsets = (Button) findViewById(R.id.btn_save_offsets);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_settings);
        relativeLayout.getBackground().setAlpha(30);

        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        int initialValenceOffset = preferences.getInt(VALENCE_OFFSET, -15);  //change
        int initialArousalOffset = preferences.getInt(AROUSAL_OFFSET, -27);   //change
        etArousal.setText(initialArousalOffset+"");
        etValence.setText(initialValenceOffset+"");

        saveOffsets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etValence.getText().length()==0){
                    Toast.makeText(SettingsActivity.this, "Valence field empty", Toast.LENGTH_SHORT).show();
                }
                else if(etArousal.getText().length()==0){
                    Toast.makeText(SettingsActivity.this, "Arousal field empty", Toast.LENGTH_SHORT).show();
                }

                else {
                    valenceOffset = Integer.parseInt(String.valueOf(etValence.getText()));
                    arousalOffset = Integer.parseInt(String.valueOf(etArousal.getText()));

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(VALENCE_OFFSET, valenceOffset);
                    editor.putInt(AROUSAL_OFFSET, arousalOffset);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Settings Saved.", Toast.LENGTH_SHORT).show();
                    sendToGetStarted();
                }
            }
        });
    }

    private void sendToGetStarted() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userName = settings.getString(USERNAME,null);
        if(userName!=null){
            automateLogin(userName);
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

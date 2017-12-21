package com.teamkassvi.ascend;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity {

    ImageView ivProfile;
    TextView tvProfile;
    int pos  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_about_us);
        relativeLayout.getBackground().setAlpha(30);

        ivProfile = (ImageView) findViewById(R.id.iv_profile);
        tvProfile = (TextView) findViewById(R.id.tv_profile);
        addProfileShowcase();
    }

    protected void addProfileShowcase(){
        final int[] imageArray = { R.drawable.profile1, R.drawable.profile2,
                R.drawable.profile3, R.drawable.profile4};

        final String[] textArray = { "Kartik Bhutani", "Suvigya Nijhawan", "Sonam Tshering", "Viha Gupta"};

        final Handler outer = new Handler();
        outer.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivProfile.animate().alpha(0).setDuration(500);
                tvProfile.animate().alpha(0).setDuration(500);
                Handler inner = new Handler();
                inner.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivProfile.setImageResource(imageArray[pos]);
                        ivProfile.animate().alpha(1).setDuration(500);
                        tvProfile.setText(textArray[pos]);
                        tvProfile.animate().alpha(1).setDuration(500);
                        pos++;
                        if (pos > imageArray.length - 1) {
                            pos = 0;
                        }
                    }
                },500);
                outer.postDelayed(this, 2500);
            }
        },1500);
    }
}

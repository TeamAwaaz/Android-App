package com.teamkassvi.ascend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailedReaction extends AppCompatActivity {

    TextView tvReaction;
    ImageView iv1, iv2, iv3, iv4;

    public static final String REACTION_TEXT = "reaction_text_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_reaction);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_detailed_reaction);
        relativeLayout.getBackground().setAlpha(40);

        tvReaction = (TextView) findViewById(R.id.tv_reaction2);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        iv4 = (ImageView) findViewById(R.id.iv4);

        String reactionText = getIntent().getExtras().getString(REACTION_TEXT);
//        Log.d("TAG123",reactionText+"");
        int imageId1 = fetchImage1(reactionText);
        int imageId2 = fetchImage2(reactionText);
        int imageId3 = fetchImage3(reactionText);
        int imageId4 = fetchImage4(reactionText);

        tvReaction.setText(reactionText);
        iv1.setImageResource(imageId1);
        iv2.setImageResource(imageId2);
        iv3.setImageResource(imageId3);
        iv4.setImageResource(imageId4);
    }

    private int fetchImage1(String reactionText) {

        int id;

        if(reactionText.equals("Convinced")){
            id = R.drawable.case1image1;
        }
        else if(reactionText.equals("Peaceful")){
            id = R.drawable.case2image1;
        }

        else{
            id = R.mipmap.ic_launcher;
        }

        return id;
    }

    private int fetchImage2(String reactionText) {

        int id;
        if(reactionText.equals("Convinced")){
            id = R.drawable.case1image2;
        }
        else if(reactionText.equals("Peaceful")){
            id = R.drawable.case2image2;
        }

        else{
            id = R.mipmap.ic_launcher;
        }

        return id;
    }

    private int fetchImage3(String reactionText) {

        int id;
        if(reactionText.equals("Convinced")){
            id = R.drawable.case1image3;
        }
        else if(reactionText.equals("Peaceful")){
            id = R.drawable.case2image3;
        }

        else{
            id = R.mipmap.ic_launcher;
        }

        return id;
    }

    private int fetchImage4(String reactionText) {

        int id;
        if(reactionText.equals("Convinced")){
            id = R.drawable.case1image4;
        }
        else if(reactionText.equals("Peaceful")){
            id = R.drawable.case2image4;
        }

        else{
            id = R.mipmap.ic_launcher;
        }

        return id;
    }
}

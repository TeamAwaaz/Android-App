package com.teamkassvi.ascend;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.andremion.music.MusicCoverView;

import java.io.IOException;

public class PlayLastAudio extends AppCompatActivity {

//    AudioWife audioWife;
    private MusicCoverView mCoverView;
    private MediaPlayer mediaPlayer = null;
    Button btnPlay;
    int flag = 0;
    float currProgress = 0;
    double audioDuration = 0;
    String filePath;
//    CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_last_audio);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_play_last_audio);
        relativeLayout.getBackground().setAlpha(50);

        flag = 0;
        RelativeLayout mPlayerContainer = (RelativeLayout) findViewById(R.id.activity_play_last_audio);

        mCoverView = (MusicCoverView) findViewById(R.id.cover);
        btnPlay = (Button) findViewById(R.id.btn_play);

        Intent intent = getIntent();
        filePath = intent.getStringExtra(StaticActivity.CREATED_TIME);

        mCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {

                if (MusicCoverView.SHAPE_CIRCLE == coverView.getShape()) {
                    coverView.start();
                }
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                coverView.morph();
            }
        });

//        audioWife = new AudioWife();
//        audioWife.init(this, Uri.parse(filePath)).useDefaultUi(mPlayerContainer,getLayoutInflater()).pause();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("LastAudioPlayLast", filePath);
//                if(filePath.endsWith("7_rec.wav")) {
//                    Log.d("LastAudioPlayLast1", filePath);
                    playSound(filePath);
                    playUI();
                    updateButton();
//                }
//                else{
//                    Toast.makeText(PlayLastAudio.this, "No audio recorded yet.", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopUI();
                initializeButton();
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                audioDuration = mediaPlayer.getDuration()/1000.0;
            }
        });

//        progressBarInitialize();



    }

//    private void progressBarInitialize() {
//        circularProgressBar = (CircularProgressBar)findViewById(R.id.progress_bar);
//        circularProgressBar.setColor(ContextCompat.getColor(this, R.color.progressBarColor));
//        circularProgressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.progressBarBackgroundColor));
//        circularProgressBar.setProgressBarWidth(getResources().getDimension(R.dimen.progress_bar_width));
//        circularProgressBar.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.progress_bar_background_width));
////        int animationDuration = ; // 2500ms = 2,5s
////        circularProgressBar.setProgressWithAnimation(0, animationDuration);
//    }

    private void initializeButton() {
        btnPlay.setText("Play");
    }

    private void updateButton() {
        if(btnPlay.getText().equals("Play")){
//            startProgressBar();
            btnPlay.setText("Pause");
        }
        else{
//            stopProgressBar();
            btnPlay.setText("Play");
        }
    }

//    private void startProgressBar() {
//        currProgress = circularProgressBar.getProgress();
//        circularProgressBar.setProgressWithAnimation(currProgress, (int) Math.ceil(audioDuration));
//    }

//    private void stopProgressBar() {
//        currProgress = circularProgressBar.getProgress();
//        circularProgressBar.setProgress(currProgress);
//    }

    private void stopUI(){
        if (mCoverView.isRunning()) {
            mCoverView.stop();
        }
    }

    private void playUI(){
        if (mCoverView.isRunning()) {
            mCoverView.stop();
        } else {
            mCoverView.morph();
        }
    }

    private void playSound(String filePath){

        if(flag==0){
            try {
                mediaPlayer.setDataSource(filePath);
//                int pos = mediaPlayer.getCurrentPosition();
                mediaPlayer.prepareAsync();
//                mediaPlayer.start();
            } catch (IOException e) {
                Log.d("ErrorAudio",e+"");
                e.printStackTrace();
            } catch (Exception e){
                Log.d("TAG123","Error: "+e);
                e.printStackTrace();
            }

            flag=1;
        }

        else if(flag==1&&!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }

        else{
            mediaPlayer.pause();
        }
    }

    @Override
    public void onBackPressed() {
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

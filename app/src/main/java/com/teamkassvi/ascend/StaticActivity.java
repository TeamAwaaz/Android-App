package com.teamkassvi.ascend;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;
import omrecorder.WriteAction;

import static com.teamkassvi.ascend.GetStartedActivity.ENTRY_FRAGMENT;
import static com.teamkassvi.ascend.GetStartedActivity.FRAG_RECORDINGS;
import static com.teamkassvi.ascend.SettingsActivity.AROUSAL_OFFSET;
import static com.teamkassvi.ascend.SettingsActivity.VALENCE_OFFSET;
import static java.lang.Thread.sleep;

public class StaticActivity extends AppCompatActivity {

    public TextView tvText;
    public TextView tvTitle1, tvTitle2 , tvEmotion, tvReaction, tvDetails;
    public String spokenText = "Loading spoken text...", emotionText, reactionText;
    public ImageView ivEmotion, ivReaction;
    public Button btnSpeakAgain;
    public ImageButton btnSpeak, btnStop, btnListen;

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
    public static final int AUDIO_FILE_REQUEST_CODE = 2;
    public static final String SPOKEN_TEXT = "spoken_text_code";
    public static final String EMOTION_TEXT = "emotion_text_code";
    public static final String REACTION_TEXT = "reaction_text_code";
    public static final String RECORDED_FILE_NAME = "_rec.wav";
    public static final String AUDIO_FILE = "audio_file";
    //----------------------------------------------
    private static final String RECORDING_URL = "https://apiv3.beyondverbal.com/v3/recording/";
    //    public static final String USERNAME = "a3ee8a8d-e9fa-4c9c-a526-28351e087a45";  //extra
//    public static final String PASSWORD = "Evu5bXRi7cqe";
    public static final String USERNAME = "add_your_own";
    public static final String PASSWORD = "add_your_own"; //add_your_own

    private static final String Auth_URL = "https://token.beyondverbal.com/token";
    public static final int INVISIBLE = 0;
    public static final int VISIBLE = 1;

    private static final String APIKey ="add_your_own";
    private Header access_token;
    private String recordingid ;
    private Button btnAnalyze;
    private Button btnSend;
    private TextView tvStatusContent;
    private TextView tvResponseContent;
    private TextView tvWait;
    private ProgressDialog progressDialog;
    private ResponseHolder responseHolder;

    //--------------------------------------------
    //--------------OM RECORDER--------------------
    Recorder recorder;
    int i=0;
    int mic = 0;
    int recording = 0;

    SharedPreferences preferences;
    public static final String CREATED_TIME = "created_time";
    String analyzeIntent;

    public static final String AROUSAL_HIGH = "high";
    public static final String AROUSAL_LOW = "low";
    public static final String AROUSAL_NEG_LOW = "neg_low";
    public static final String AROUSAL_NEG_HIGH = "neg_high";
    public static final String VALENCE_HIGH = "high";
    public static final String VALENCE_NEG = "neg";
    public static final String EM_EXCITED = "Excited";
    public static final String EM_HAPPY = "Happy";
    public static final String EM_ANGRY = "Angry";
    public static final String EM_NERVOUS = "Nervous";
    public static final String EM_DISAPPOINTED = "Disappointed";
    public static final String EM_SAD = "Sad";
    public static final String EM_PEACE = "Peace";

    public static final String RE_EXCITED = "Maintain";
    public static final String RE_HAPPY = "Maintain(Stay Happy)";
    public static final String RE_ANGRY = "Suppress";
    public static final String RE_NERVOUS = "Keep Calm";
    public static final String RE_DISAPPOINTED = "Withdraw";
    public static final String RE_SAD = "Seek Comfort";
    public static final String RE_PEACE = "Maintain(Stay Calm)";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);

        i=0;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
        relativeLayout.getBackground().setAlpha(30);

        tvText = (TextView) findViewById(R.id.tv_text);
        btnSpeak = (ImageButton) findViewById(R.id.btn_speak);
        btnListen = (ImageButton) findViewById(R.id.btn_listen);
        btnStop = (ImageButton) findViewById(R.id.btn_stop);
        tvTitle1 = (TextView) findViewById(R.id.tv_title1);
        tvTitle2 = (TextView) findViewById(R.id.tv_title2);
        tvEmotion = (TextView) findViewById(R.id.tv_emotion);
        tvReaction = (TextView) findViewById(R.id.tv_reaction);
        tvDetails = (TextView) findViewById(R.id.tv_details);
        ivEmotion = (ImageView) findViewById(R.id.iv_emotion);
        ivReaction = (ImageView) findViewById(R.id.iv_reaction);
        btnSpeakAgain = (Button) findViewById(R.id.btn_speak_again);

        //-----------------------------------------------
        responseHolder = new ResponseHolder();
        tvResponseContent = (TextView) findViewById(R.id.tv_response_content);
        tvStatusContent = (TextView) findViewById(R.id.tv_status_content);
        tvWait = (TextView) findViewById(R.id.tv_wait);

        btnSend = (Button) findViewById(R.id.btn_send);
        btnAnalyze = (Button) findViewById(R.id.btn_analyze);

        //-----------------------------------------------


        //-----------------------------------------------
        //OM Recorder -
//        setupRecorder();

        //to store recent file name
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String createdTime = preferences.getString(CREATED_TIME,"invalid");
        Log.d("LastAudioOnCreate", createdTime);

        try {
            analyzeIntent = getIntent().getExtras().getString(AUDIO_FILE);
            if (!analyzeIntent.equals("null")) {
                btnSend.performClick();
            }
        }
        catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        if(recording==1) {
            btnStop.performClick();
        }

        this.finish();
        super.onBackPressed();
    }

    public void initializeSpeechRecog(){
        PackageManager pm = getPackageManager();
        List activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        if(activities.size()!=0){
            Toast.makeText(this, "Good to go!", Toast.LENGTH_SHORT).show();
        }
        else{
            btnSpeak.setEnabled(false);
//            btnSpeak.setText("Recognizer not present");
        }
    }

    public void startVoiceRecognitionActivity(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech Recognition Demo");
        startActivityForResult(intent,VOICE_RECOGNITION_REQUEST_CODE);
    }

    //--------------------OM RECORDER--------------------

    private void setupRecorder() {

        new Runnable(){
            @Override
            public void run() {
                if(recorder!=null){
                    recorder = null;
                }
                recorder = OmRecorder.wav(
                        new PullTransport.Noise(mic(), new PullTransport.OnAudioChunkPulledListener() {
                            @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                                changeColor(1);
                            }
                        },new WriteAction.Default(),
                                new Recorder.OnSilenceListener() {
                                    @Override public void onSilence(long silenceTime) {
//                                        Log.e("silenceTime", String.valueOf(silenceTime));
//                                        Toast.makeText(StaticActivity.this, "silence of " + silenceTime + " detected",
//                                                Toast.LENGTH_SHORT).show();
                                    }
                                }, 200), recordedFile());
            }
        }.run();

    }

    private PullableSource mic() {
        return new PullableSource.NoiseSuppressor(
                new PullableSource.Default(
                        new AudioRecordConfig.Default(
                                MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                                AudioFormat.CHANNEL_IN_MONO, 8000
                        )
                )
        );
    }

    private void changeColor(final int status){


        if(status==0){
            btnSpeak.setImageResource(R.drawable.ic_mic2);
        }
        else{
            if(i<10){
                i++;
//                btnSpeak.setImageResource(R.drawable.ic_mic1);
            }
            else{
                i=0;
                if(mic==0) {
                    mic = 1;
                    btnSpeak.setImageResource(R.drawable.ic_mic1);
                }
                else{
                    mic = 0;
                    btnSpeak.setImageResource(R.drawable.ic_mic2);
                }
            }
        }
//        final int[] imageArray = new int[]{R.drawable.ic_mic1, R.drawable.ic_mic2};
//        final Handler handler = new Handler();
//        Runnable runnable = null;
//        final Runnable finalRunnable = runnable;
//        runnable = new Runnable(){
//            int i=0;
//            @Override
//            public void run() {
//                btnSpeak.setImageResource(imageArray[i]);
//                i++;
//                if(i>imageArray.length-1){
//                    i=0;
//                }
//
//                if(status==1) {
//                    handler.postDelayed(this, 500);
//                }
//                else {
//                    handler.removeCallbacks(finalRunnable);
//                }
//            }
//        };
//        if(status==1) {
//            handler.postDelayed(runnable, 500);
//        }
//        else{
//            handler.removeCallbacksAndMessages(runnable);
//        }
    }

    private void animateVoice(final float maxPeak) {
        new Runnable(){
            @Override
            public void run() {
                btnSpeak.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
            }
        }.run();
    }


    @NonNull
    private String recentFile() {

        String createdTimeWithSpaces = preferences.getString(CREATED_TIME,"invalid"); //check to give toast.
        String createdTime = "";
        int count = 0;
        for(int i=0;i<createdTimeWithSpaces.length();i++){
            if(count==14){
                break;
            }
            char curr = createdTimeWithSpaces.charAt(i);
            if(curr!=' '&&curr!=':'&&curr!='+'){
                createdTime+=curr;
                count++;
            }
        }
        String fileLocation = String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        String filePath = fileLocation+"/"+createdTime+RECORDED_FILE_NAME;
//        String filePath = fileLocation+"/"+"audio1.wav";
        return filePath;
    }

    @NonNull
    private File recordedFile() {
        Date createdTimeWithSpaces = new Date();
        String createdTime = "";
        int count = 0;
        for(int i=0;i<(createdTimeWithSpaces.toString()).length();i++){
            if(count==14){
                break;
            }
            char curr = createdTimeWithSpaces.toString().charAt(i);
            if(curr!=' '&&curr!=':'&&curr!='+'){
                createdTime+=curr;
                count++;
            }
        }
        String fileLocation = String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        String filePath = fileLocation+"/" + createdTime + RECORDED_FILE_NAME;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CREATED_TIME,createdTime.toString());
        editor.commit();
//        preferences.edit().putString(CREATED_TIME,createdTime.toString());
        String createdTimeString = preferences.getString(CREATED_TIME,"invalid");
        Log.d("LastAudioWhenRecorded", createdTimeString);
        return new File(filePath);
    }


    //-----------------------------------------------------
    public void startVoiceRecordActivity(){
        final MediaRecorder recorder = new MediaRecorder();
        ContentValues values = new ContentValues(3);

        String fileLocation = String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        String filePath = fileLocation+"/"+RECORDED_FILE_NAME;
        values.put(MediaStore.MediaColumns.TITLE, RECORDED_FILE_NAME);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        Log.d("TAG123", fileLocation);
        //file location - Android/data/.....
        recorder.setOutputFile(filePath);
        try {
            recorder.prepare();
        } catch (Exception e){
            e.printStackTrace();
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(StaticActivity.this);
        mProgressDialog.setTitle("Recording...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setButton("Stop recording", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mProgressDialog.dismiss();
                recorder.stop();
                recorder.release();
            }
        });

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
            public void onCancel(DialogInterface p1) {
                recorder.stop();
                recorder.release();
            }
        });
        recorder.start();
        mProgressDialog.show();
    }

    private void playLastAudio(){

//        Intent intent = new Intent(this,ListenAudios.class);
//        String createdTime = preferences.getString(CREATED_TIME,"invalid");
//        Log.d("LastAudioHear", recentFile());
//
//        intent.putExtra(CREATED_TIME,recentFile());
//        startActivity(intent);

        Intent intent = new Intent(StaticActivity.this, ViewpagerActivity.class);
        intent.putExtra(ENTRY_FRAGMENT, FRAG_RECORDINGS);
        startActivity(intent);
    }



    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_listen:
                playLastAudio();
                break;

            case R.id.btn_stop:
                try {
                    if(recorder!=null) {
                        recorder.stopRecording();
                        btnSpeak.setClickable(true);
                        recording = 0;
                        final String currentPath = recentFile();
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        final double[] duration = new double[1];
                        mediaPlayer.setDataSource(currentPath);
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                duration[0] = mediaPlayer.getDuration()/1000.0;
                                if(duration[0] >10) {
                                    btnSend.setClickable(true);
                                    updateAnalyseButton(VISIBLE);
                                    Toast.makeText(StaticActivity.this, "Audio Saved. Ready to Analyze", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(StaticActivity.this, "Audio file should be of more than 10 seconds. File deleted.",
                                            Toast.LENGTH_SHORT).show();
                                    File file = new File(currentPath);
                                    file.delete();
                                }
                            }
                        });
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                btnSpeak.post(new Runnable() {
                    @Override public void run() {
                        animateVoice(0);
                        changeColor(0);
                    }
                });

                break;

            case R.id.btn_speak:
            case R.id.btn_speak_again:
//                startVoiceRecognitionActivity();
//                startVoiceRecordActivity();
//                Log.d("TAG123","RecordingStarted1: ");
                setupRecorder();
                recorder.startRecording();
                btnSend.setClickable(false);
                btnSpeak.setClickable(false);
                recording = 1;
                Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();
//                Log.d("TAG123","RecordingStarted2: ");
//                skipSilence.setEnabled(false);
                break;

            case R.id.btn_send:
                generateSpokenText();
                hitServer();

                break;
        }
    }

    private void updateAnalyseButton(int option) {
        switch (option){
            case 0 :
                btnSend.animate().alpha(0).setDuration(500);
                break;
            case 1:
                btnSend.setText("Analyze");
                btnSend.animate().alpha(1).setDuration(500);
                break;
        }
    }

    private void hitServer() {

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {

            final double[] duration = new double[1];
//            Log.d("LastAudioAnalysis",filePath);
            if(analyzeIntent.equals("null")) {
                mediaPlayer.setDataSource(recentFile());
            }
            else{
                mediaPlayer.setDataSource(analyzeIntent);
            }
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    duration[0] = mediaPlayer.getDuration()/1000.0;
//                    Log.d("TAG123",duration[0]+"");
                    if(duration[0] >10) {
                        btnSend.setText("Re-analyze");
                        new ServerConnection().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    else{
                        Toast.makeText(StaticActivity.this, "Audio file should be of more than 10 seconds.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "No audio file recorded yet", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {

            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            String first = matches.get(0);
//            Log.d("TAG123", "  " + matches + "");

//            updateViews(matches);
        }
    }

    public void updateViews(final String emotionText){

        ivEmotion.animate().alpha(0).setDuration(500);
        ivReaction.animate().alpha(0).setDuration(500);
        tvText.animate().alpha(0).setDuration(500);
        tvEmotion.animate().alpha(0).setDuration(500);
        tvReaction.animate().alpha(0).setDuration(500);
//        btnSpeak.animate().alpha(0).setDuration(500);
//        emotionText = fetchEmotionText(spokenText);
        Log.d("TAGStaticUpdate: ", emotionText + "");
        fetchReactionText(emotionText);
    }

    private String fetchReactionText(final String emotionText) {

        //Create mapping using emotionText.
//        emotionText = "abc";
        String reactionText = "";
        if(emotionText.equals(EM_EXCITED)){
            reactionText = RE_EXCITED;
        }
        else if(emotionText.equals(EM_HAPPY)){
            reactionText = RE_HAPPY;
        }
        else if(emotionText.equals(EM_ANGRY)){
            reactionText = RE_ANGRY;
        }
        else if(emotionText.equals(EM_NERVOUS)){
            reactionText = RE_NERVOUS;
        }
        else if(emotionText.equals(EM_DISAPPOINTED)){
            reactionText = RE_DISAPPOINTED;
        }
        else if(emotionText.equals(EM_SAD)){
            reactionText = RE_SAD;
        }
        else if(emotionText.equals(EM_PEACE)){
            reactionText = RE_PEACE;
        }
        final int emotionId = fetchEmotionImage(emotionText);
        final int reactionId = fetchReactionImage(reactionText, emotionText, emotionId);

        return reactionText;
    }

    private int fetchEmotionImage(String text) {

        //Create Mapping
        int emotionId = 0;
        if(text.equals(EM_EXCITED)){
            emotionId = R.drawable.img_em_excited;
        }
        else if(text.equals(EM_HAPPY)){
            emotionId = R.drawable.img_em_happy;
        }
        else if(text.equals(EM_ANGRY)){
            emotionId = R.drawable.img_em_angry;
        }
        else if(text.equals(EM_NERVOUS)){
            emotionId = R.drawable.img_em_nervous;
        }
        else if(text.equals(EM_DISAPPOINTED)){
            emotionId = R.drawable.img_em_disappointed;
        }
        else if(text.equals(EM_SAD)){
            emotionId = R.drawable.img_em_sad;
        }
        else if(text.equals(EM_PEACE)){
            emotionId = R.drawable.img_em_peace;
        }

        return emotionId;
    }

    private int fetchReactionImage(String reactionText, final String emotionText, final int emotionId) {

        //Create Mapping
        int reactionId;

        switch (emotionText) {
            case EM_EXCITED:
                reactionId = R.drawable.img_re_excited;
                break;
            case EM_HAPPY:
                reactionId = R.drawable.img_re_happy;
                break;
            case EM_ANGRY:
                reactionId = R.drawable.img_re_angry;
                break;
            case EM_NERVOUS:
                reactionId = R.drawable.img_re_nervous;
                break;
            case EM_DISAPPOINTED:
                reactionId = R.drawable.img_re_disappointment;
                break;
            case EM_SAD:
                reactionId = R.drawable.img_re_sadness;
                break;
            case EM_PEACE:
                reactionId = R.drawable.img_re_peace;
                break;
            default:
                reactionId = 0;
                break;
        }

        Handler handler = new Handler();
        final String finalReactionText = reactionText;
        final int finalReactionId = reactionId;
        Log.d("TAGReactionId: ", reactionId+"");
        handler.postDelayed(new Runnable() {
            public void run() {

                //set new images and text.

                try {
//                    tvText.setText(spokenText);
                    tvEmotion.setText(emotionText);
                    tvReaction.setText(finalReactionText);
                    ivEmotion.setImageResource(emotionId);
                    ivReaction.setImageResource(finalReactionId);
                }
                catch (Exception e){
                    tvText.setText("---Some error occurred. Please try again.---");
                    tvEmotion.setText("---");
                    tvReaction.setText("---");
//                ivEmotion.setImageResource(emotionId);
//                ivReaction.setImageResource(reactionId);
                }
                tvTitle1.animate().alpha(1).setDuration(500);
                tvTitle2.animate().alpha(1).setDuration(500);
                ivEmotion.animate().alpha(1).setDuration(500);
                ivReaction.animate().alpha(1).setDuration(500);
                tvText.animate().alpha(1).setDuration(500);
                tvEmotion.animate().alpha(1).setDuration(500);
                tvReaction.animate().alpha(1).setDuration(500);
                tvDetails.animate().alpha(1).setDuration(500);

//                btnSpeakAgain.animate().alpha(1).setDuration(500);
            }
        }, 500);

        return reactionId;
    }

    public void onClickDetailsLink(View v){
        Intent intent= new Intent(this, DetailedReaction.class);
//        intent.putExtra(SPOKEN_TEXT, spokenText);
//        intent.putExtra(EMOTION_TEXT,emotionText);
        intent.putExtra(REACTION_TEXT,reactionText);

        startActivity(intent);
    }

    //---------------------------------------------------------

    private ResponseHolder postByAction()
    {
        //System.setProperty("http.proxyHost", "127.0.0.1");
        //System.setProperty("http.proxyPort", "8888");

        HttpActivity httpa = new HttpActivity();

        getToken();
        HttpEntity entity = getEntityForUpstream();
        responseHolder = httpa.doPost(RECORDING_URL + "start", access_token, entity);

        if (responseHolder.content != null){
            recordingid = getRecordingid(responseHolder.content);
            responseHolder = httpa.doPost(RECORDING_URL + recordingid, access_token, getEntityForSendFile());
        }
        return responseHolder;
    }

//    public void GoStream() {
//
//        Log.d("never here: ", "never here");
//        tvWait.post(new Runnable() {
//            public void run() {
//                tvWait.setText("Wait");   //change
//            }
//        });
//
//        Thread stream =new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                HttpActivity httpac = new HttpActivity();
//                final ResponseHolder hol = httpac.doPost(RECORDING_URL + recordingid, access_token, getEntityForSendFile());
//
//
//                tvResponseContent.post(new Runnable() {
//                    public void run() {
//                        CharSequence text = tvResponseContent.getText();
//                        tvResponseContent.setText("Full analysis::::"+hol.activity_get_started+text);   //change
//
//                        updateAnalysisUI(hol.activity_get_started);
//                        Log.d("TAG123","Full analysis::::"+hol.activity_get_started);
////                        Log.d("TAG123",text+"");
//                    }
//                });
//
//            }
//        });
//
//        stream.start();
//        //****
//        // When post is sended file anylize file parts (Asyncronic send requests for analysis with FromMs milisecond from start file )
//        // **/
//        Analyze();
//    }

    private void updateAnalysisUI(String content) {

//        generateSpokenText();
        final String emotionText = getEmotionText(content);
//        Log.d("TAGStatic: ", emotionText+"");
//        updateViews(emotionText);
//        animateButtons();
    }

    private String getEmotionText(String content) {
        String emotionText = null;
        JSONObject fullJson;
        try {
            fullJson = new JSONObject(content);
            JSONObject result = fullJson.getJSONObject("result");
            JSONArray segments = result.getJSONArray("analysisSegments");
            JSONObject analysis = ((JSONObject)segments.get(0)).getJSONObject("analysis");

            JSONObject Temper = analysis.getJSONObject("Temper");
            String temperValue = Temper.getString("Value");

            JSONObject Valence = analysis.getJSONObject("Valence");
            int valenceValue = Valence.getInt("Value");
            int valenceOffset = preferences.getInt(VALENCE_OFFSET,0);

//            Log.d("TAGValence: ",valenceOffset+"");
            int finalValence = valenceValue + valenceOffset;


            JSONObject Arousal = analysis.getJSONObject("Temper");
            int arousalValue = Arousal.getInt("Value");
            int arousalOffset = preferences.getInt(AROUSAL_OFFSET,0);

            int finalArousal = arousalValue + arousalOffset;

//            emotionText = "Valence: " + valenceValue + " Arousal: " + arousalValue + "Final Valence: "+ finalValence +
//                            "Final Arousal: "+finalArousal;
            Toast.makeText(this, "Valence: " + finalValence + " Arousal: "+finalArousal, Toast.LENGTH_SHORT).show();
//            tvReaction.setText("Valence: " + finalValence + " Arousal: "+finalArousal);

            emotionText = getEmotionText(finalValence, finalArousal);
//            JSONObject Mood = analysis.getJSONObject("Mood");
//            JSONObject Group11 = Mood.getJSONObject("Group11");
//            JSONObject Primary = Group11.getJSONObject("Primary");
//            emotionText = Primary.getString("Phrase");

            Log.d("TAG123",emotionText);
        } catch (JSONException e) {
            Toast.makeText(this, "Some problem at Server-end", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return emotionText;
    }

    private String getEmotionText(int valence, int arousal) {
        String arousalLevel = "";
        String valenceLevel = "";

        if(arousal>=10){
            arousalLevel = AROUSAL_HIGH;
        }
        else{
            arousalLevel = AROUSAL_LOW;
        }
//        if(arousal>=50){
//            arousalLevel = AROUSAL_HIGH;
//        }
//        else if(arousal>=10){
//            arousalLevel = AROUSAL_LOW;
//        }
//        else if(arousal>=-20){
//            arousalLevel = AROUSAL_NEG_LOW;
//        }
//        else{
//            arousalLevel = AROUSAL_NEG_HIGH;
//        }
//
        if(valence>=0){
            valenceLevel = VALENCE_HIGH;
        }
        else{
            valenceLevel = VALENCE_NEG;
        }

        return getEmotionText(valenceLevel,arousalLevel);

    }

    private String getEmotionText(String valenceLevel, String arousalLevel) {

        String emotion = "";

        if(arousalLevel.equals(AROUSAL_LOW)){
            emotion = EM_PEACE;
        }
        else if(valenceLevel.equals(VALENCE_HIGH)){
            emotion = EM_HAPPY;
        }
        else if(valenceLevel.equals(VALENCE_NEG)){
            emotion = EM_ANGRY;
        }
//        //1
//        if(valenceLevel.equals(VALENCE_HIGH)&&arousalLevel.equals(AROUSAL_HIGH)){
//            emotion = EM_EXCITED;
//        }
//        else if(valenceLevel.equals(VALENCE_HIGH)&&arousalLevel.equals(AROUSAL_LOW)){
//            emotion = EM_HAPPY;
//        }
//
//        //2
//        else if(valenceLevel.equals(VALENCE_NEG)&&arousalLevel.equals(AROUSAL_HIGH)){
//            emotion = EM_ANGRY;
//        }
//        else if(valenceLevel.equals(VALENCE_NEG)&&arousalLevel.equals(AROUSAL_LOW)){
//            emotion = EM_NERVOUS;
//        }
//
//        //3
//        else if(valenceLevel.equals(VALENCE_NEG)&&arousalLevel.equals(AROUSAL_NEG_LOW)){
//            emotion = EM_DISAPPOINTED;
//        }
//        else if(valenceLevel.equals(VALENCE_NEG)&&arousalLevel.equals(AROUSAL_NEG_HIGH)){
//            emotion = EM_SAD;
//        }
//
//        //4
//        else if(valenceLevel.equals(VALENCE_HIGH)&&arousalLevel.equals(AROUSAL_HIGH)){
//            emotion = EM_PEACE;
//        }

        Log.d("TAGStatic: ", emotion+"");
        updateViews(emotion);
        animateButtons();
        return emotion;
    }

    private void generateSpokenText() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    SpeechToText service = new SpeechToText();
                    service.setUsernameAndPassword(USERNAME, PASSWORD);

                    File audio;
                    if(analyzeIntent.equals("null")) {
                        audio = new File(recentFile());
                    }
                    else{
                        audio = new File(analyzeIntent);
                    }

                    RecognizeOptions options = new RecognizeOptions.Builder()
                            .contentType(HttpMediaType.AUDIO_WAV).model("en-US_NarrowbandModel")
                            .build();

                    SpeechResults transcript = service.recognize(audio, options).execute();
                    Log.d("TAG123Transcript",transcript.toString());
                    final String text = getSpeechTextFromJson(transcript);
                    Log.d("TAG123Text",text);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("UIThreadText", text);
                            updateSpokenText(text);
                        }
                    });

                }
                catch (Exception e){
                    Log.d("TAG123Text",e+"");
                }

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateSpokenText(final String text){
        try {
            tvText.setText(text);
        } catch (Exception e) {
            tvText.setText("---Some error occurred. Please try again.---");
        }
    }

    private String getSpeechTextFromJson(SpeechResults transcript) {

        try {
            String fullText = "";
            JSONObject transcriptObj = new JSONObject(transcript.toString());
            JSONArray results = transcriptObj.getJSONArray("results");

            for(int i=0;i<results.length();i++){
                JSONArray alternatives = ((JSONObject)results.get(i)).getJSONArray("alternatives");
                fullText += ((JSONObject)alternatives.get(0)).getString("transcript");
            }

            return fullText;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Error loading.";
    }

    private void animateButtons() {
        btnSpeak.animate().translationY(530).setDuration(400);
        btnListen.animate().translationY(530).setDuration(400);
        btnStop.animate().translationY(530).setDuration(400);
    }

    private long FromMs = 0;

    public void Analyze(){

        Log.d("never here: ", "never here");
        final Timer myTimer = new Timer();
        long delay = 0;
        long period=5000;
        //final Handler uiHandler = new Handler();

        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                HttpActivity httpac = new HttpActivity();
                try{

                    tvWait.post(new Runnable() {
                        public void run() {
                            tvWait.append(".");
                        }
                    });

                    final ResponseHolder hol = httpac.doGet(RECORDING_URL + recordingid + "/analysis?fromMs=" + FromMs, access_token);

                    String status = getJName(hol.content, "status");
                    String sesionStatus = getsesionStatus(hol.content);
                    String f = getDuration(hol.content);
                    if (status.equals("success")) {
                        FromMs = Long.parseLong(f.replace(".0", ""));
                    }
                    if (sesionStatus!=null && sesionStatus.equals("Done")) {
                        myTimer.cancel();
                        tvWait.post(new Runnable() {
                            public void run() {
                                tvWait.setText("");
                            }
                        });
                    }

                    tvResponseContent.post(new Runnable() {
                        public void run() {
//							CharSequence text = responseContentTextView.getText();
//							responseContentTextView.setText("\n....\n"+hol.activity_get_started+text);
                            tvResponseContent.append("\n....\n"+hol.content);
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        },delay,period);


    }

    protected void ResponseResult(ResponseHolder rs){

        tvStatusContent.setText(rs.responseString);   //change
        if (rs.content != null)
        {
            //CharSequence text = responseContentTextView.getText();
            //responseContentTextView.setText("\n-----------------\n" + rs.activity_get_started + text);
            tvResponseContent.append("\n-----------------\n" + rs.content);
        }

    }
    private String getsesionStatus(String response){
        if (response == null)
            return null;
        try {
            JSONObject json = new JSONObject(response);
            String duration = json.getJSONObject("result").getString("sessionStatus");

            return duration;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getDuration(String response){
        if (response == null)
            return null;
        try {
            JSONObject json = new JSONObject(response);
            String duration = json.getJSONObject("result").getString("duration");

            return duration;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getJName(String response,String name){
        if (response == null)
            return null;
        try {
            JSONObject json = new JSONObject(response);
            String recordingid = json.getString(name);

            return recordingid;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }
    private String getRecordingid(String response) {
        if (response == null)
            return null;
        try {
            JSONObject json = new JSONObject(response);
            String recordingid = json.getString("recordingId");

            return recordingid;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Called when one of the buttons is pressed getUpstream or SendFile
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
//    @Override
//    public void onClick(View v)
//    {
//        new ServerConnection().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, v.getId());
//    }


    /**
     * Creates background thread to connect to server in order to avoid blocking
     * UI thread
     *
     */
    private class ServerConnection extends AsyncTask<Void, Void, ResponseHolder>
    {

        //ProgressDialog progressDialog;

        @Override
        protected ResponseHolder doInBackground(Void... voids) {
            return postByAction();
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(StaticActivity.this);
            progressDialog.setMessage("please wait...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ResponseHolder responseHolder)
        {
            tvStatusContent.setText(responseHolder.responseString);    //change
            if (responseHolder.content != null)
            {
                //setButtonByAction(responseHolder.actionId);
                CharSequence text = tvResponseContent.getText();
                tvResponseContent.setText(text + responseHolder.content);   //change

                updateAnalysisUI(responseHolder.content);
                Log.d("TAG1234",text + responseHolder.content);
            }

            progressDialog.dismiss();
            super.onPostExecute(responseHolder);
        }
    }

//	/**
//	 * Disable send_file_button until receiving upstream url from server
//	 * @param actionId the button id
//	 */

    /**
     * @return the WAV file from local resource
     */
    private HttpEntity getEntityForSendFile()
    {

        // Fetches file from local resources.
//        fetchFile();
//        FileInputStream fileInputStream = null;
//        try {
//            fileInputStream = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        InputStream raw = null;

        try {
            if(analyzeIntent.equals("null")) {
                raw = new FileInputStream(recentFile());
            }
            else{
                raw = new FileInputStream(analyzeIntent);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        InputStream raw = getResources().openRawResource(R.raw.sample);
        InputStreamEntity reqEntity = new InputStreamEntity(raw, -1);
        return reqEntity;
    }

//    private void fetchFile(){
//        Intent intent = new Intent();
//        intent.setType("audio/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent,AUDIO_FILE_REQUEST_CODE);
//    }
    /**
     * @return the configuration data for get upstream url
     */
    private HttpEntity getEntityForUpstream()
    {
        StringEntity se = null;
        try
        {
            se = new StringEntity(getConfigData());
            se.setContentType("application/json; charset=UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return se;
    }


    /**
     * Create the https client with all configuration settings
     *
     * @return
     */

    private void getToken() {
        if(access_token!=null)
            return;
        HttpActivity httpa = new HttpActivity();
        String jsonToken = httpa.doPost(Auth_URL, null, getEntityForAccessToken()).content;
        if (jsonToken == null)
            return ;
        JSONObject jsonObject;
        Header header = null;
        try {
            jsonObject = new JSONObject(jsonToken);
            header = new BasicHeader("Authorization",
                    jsonObject.getString("token_type")+" "+jsonObject.getString("access_token"));
            Log.i("header", header.getName() + "  " + header.getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        access_token = header;
    }
    private HttpEntity getEntityForAccessToken() {

        String body = String.format("apikey=%s&grant_type=%s", APIKey, "client_credentials");

        StringEntity se = null;
        try {
            se = new StringEntity(body);
            se.setContentType("Content-Type:application/x-www-form-urlencoded");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return se;

    }
    /**
     * Sets configuration data for POST to server to receive upstream url
     *
     * @return
     */
    private String getConfigData()
    {
        try
        {
            // Instantiate a JSON Object and fill with Configuration Data
            // (Currently set to Auto Config)
            JSONObject inner_json = new JSONObject();
            inner_json.put("type", "WAV");
            inner_json.put("channels", 1);
            inner_json.put("sample_rate", 0);
            inner_json.put("bits_per_sample", 0);
            inner_json.put("auto_detect", true);
            JSONObject json = new JSONObject();
            json.put("data_format", inner_json);

            return json.toString();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //----------------------------------------------------------


}
package teamawaazdtu.com.awaaz;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

public class MainActivity extends AppCompatActivity {

    public TextView tvText;
    public TextView tvTitle1, tvTitle2 , tvEmotion, tvReaction, tvDetails;
    public String spokenText, emotionText, reactionText;
    public ImageView ivEmotion, ivReaction;
    public Button btnSpeakAgain;
    public ImageButton btnSpeak, btnStop, btnListen;

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
    public static final int AUDIO_FILE_REQUEST_CODE = 2;
    public static final String SPOKEN_TEXT = "spoken_text_code";
    public static final String EMOTION_TEXT = "emotion_text_code";
    public static final String REACTION_TEXT = "reaction_text_code";
    public static final String RECORDED_FILE_NAME = "audio1.wav";

    //----------------------------------------------
    private static final String RECORDING_URL = "https://apiv3.beyondverbal.com/v3/recording/";

    private static final String Auth_URL = "https://token.beyondverbal.com/token";


    private static final String APIKey ="your_own_key_here";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        i=0;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
        relativeLayout.getBackground().setAlpha(30);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);


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
        setupRecorder();
    }

//    @Override
//    public void onBackPressed() {
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_reports) {
//            Intent intent = new Intent(this,WeeklyReports.class);
//            startActivity(intent);
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

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
                        new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                            @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                                changeColor(1);
                            }
                        }), file());
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

//    public void convertToRequiredFormat(){
//
//        var output = ConvertWavTo8000Hz16BitMonoWav(input);
//        File.WriteAllBytes("C:/output.wav", output);
//    }

    @NonNull
    private File file() {
        String fileLocation = String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        String filePath = fileLocation+"/"+RECORDED_FILE_NAME;
        return new File(filePath);
    }

    public void startVoiceRecording(){

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

        final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
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

        Intent intent = new Intent(this,PlayLastAudio.class);
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
                    }
//                    changeColor(0);
                    Toast.makeText(this, "Audio Saved. Ready to Analyze", Toast.LENGTH_SHORT).show();
//                    Log.d("TAG123","RecordingStopped: ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (Exception e){
                    Toast.makeText(this, "Already Stopped", Toast.LENGTH_SHORT).show();
//                    Log.d("TAG123","Exception Type 2 : "+ e);
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
                Log.d("TAG123","RecordingStarted1: ");
                setupRecorder();
                recorder.startRecording();
//                changeColor(1);
                Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();
                Log.d("TAG123","RecordingStarted2: ");
//                skipSilence.setEnabled(false);
                break;

            case R.id.btn_send:
            case R.id.btn_analyze:

                hitServer(v);

                break;
        }
    }

    private void hitServer(final View v) {
        String fileLocation = String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        String filePath = fileLocation + "/" + RECORDED_FILE_NAME;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {

            final double[] duration = new double[1];
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    duration[0] = mediaPlayer.getDuration()/1000.0;
//                    Log.d("TAG123",duration[0]+"");
                    if(duration[0] >10) {
                        new ServerConnection().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, v.getId());
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Audio file should be of more than 10 seconds.", Toast.LENGTH_SHORT).show();
                    }
                }
            });



        } catch (IOException e) {
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

    public void updateViews(){

        ivEmotion.animate().alpha(0).setDuration(500);
        ivReaction.animate().alpha(0).setDuration(500);
        tvText.animate().alpha(0).setDuration(500);
        tvEmotion.animate().alpha(0).setDuration(500);
        tvReaction.animate().alpha(0).setDuration(500);
//        btnSpeak.animate().alpha(0).setDuration(500);
//        emotionText = fetchEmotionText(spokenText);
        reactionText = fetchReactionText();
        final int emotionId = fetchEmotionImage(emotionText);
        final int reactionId = fetchReactionImage(reactionText);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                //set new images and text.

                try {
                    tvText.setText(spokenText);
                    tvEmotion.setText(emotionText);
                    tvReaction.setText(reactionText);
                    ivEmotion.setImageResource(emotionId);
                    ivReaction.setImageResource(reactionId);
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
    }

    private String fetchReactionText() {

        //Create mapping using emotionText.
        String reactionText;
        reactionText = "Peaceful";
        return reactionText;
    }

    private int fetchEmotionImage(String text) {

        //Create Mapping
        int emotionId;
        emotionId = R.drawable.case1emotion;
        return emotionId;
    }

    private int fetchReactionImage(String text) {

        //Create Mapping
        int reactionId;
        reactionId = R.drawable.case1reaction;
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

    private ResponseHolder postByAction(int buttonId)
    {
        //System.setProperty("http.proxyHost", "127.0.0.1");
        //System.setProperty("http.proxyPort", "8888");

        HttpActivity httpa = new HttpActivity();

        switch (buttonId)
        {
            case R.id.btn_analyze:   // add btn
                getToken();
                responseHolder = httpa.doPost(RECORDING_URL + "start", access_token, getEntityForUpstream());

                if (responseHolder.content != null){
                    recordingid = getRecordingid(responseHolder.content);
                    GoStream();

                }
                break;
            case R.id.btn_send:  //add btn
                getToken();
                HttpEntity entity = getEntityForUpstream();
                responseHolder = httpa.doPost(RECORDING_URL + "start", access_token, entity);

                if (responseHolder.content != null){
                    recordingid = getRecordingid(responseHolder.content);
                    responseHolder = httpa.doPost(RECORDING_URL + recordingid, access_token, getEntityForSendFile());
                }


        }
        return responseHolder;
    }

    public void GoStream() {


        tvWait.post(new Runnable() {
            public void run() {
                tvWait.setText("Wait");   //change
            }
        });

        Thread stream =new Thread(new Runnable() {
            @Override
            public void run() {

                HttpActivity httpac = new HttpActivity();
                final ResponseHolder hol = httpac.doPost(RECORDING_URL + recordingid, access_token, getEntityForSendFile());


                tvResponseContent.post(new Runnable() {
                    public void run() {
                        CharSequence text = tvResponseContent.getText();
                        tvResponseContent.setText("Full analysis::::"+hol.content+text);   //change

                        updateAnalysisUI(hol.content);
                        Log.d("TAG123","Full analysis::::"+hol.content);
//                        Log.d("TAG123",text+"");
                    }
                });

            }
        });

        stream.start();
        //****
        // When post is sended file anylize file parts (Asyncronic send requests for analysis with FromMs milisecond from start file )
        // **/
        Analyze();
    }

    private void updateAnalysisUI(String content) {

        spokenText = getSpokenText();
        emotionText = getEmotionText(content);
        animateButtons();
        updateViews();
    }

    private String getEmotionText(String content) {
        String emotionText = null;
        JSONObject fullJson;
        try {
            fullJson = new JSONObject(content);
            JSONObject result = fullJson.getJSONObject("result");
            JSONArray segments = result.getJSONArray("analysisSegments");
            JSONObject analysis = ((JSONObject)segments.get(0)).getJSONObject("analysis");
            JSONObject Mood = analysis.getJSONObject("Mood");
            JSONObject Group11 = Mood.getJSONObject("Group11");
            JSONObject Primary = Group11.getJSONObject("Primary");
            emotionText = Primary.getString("Phrase");

            Log.d("TAG123",emotionText);
        } catch (JSONException e) {
            Toast.makeText(this, "Some problem at Server-end", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return emotionText;
    }

    private String getSpokenText() {
        String s = "Spoken Text";
        return s;
    }

    private void animateButtons() {
        btnSpeak.animate().translationY(530).setDuration(400);
        btnListen.animate().translationY(530).setDuration(400);
        btnStop.animate().translationY(530).setDuration(400);
    }

    private long FromMs = 0;

    public void Analyze(){

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
//							responseContentTextView.setText("\n....\n"+hol.content+text);
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
            //responseContentTextView.setText("\n-----------------\n" + rs.content + text);
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
    private class ServerConnection extends AsyncTask<Integer, Void, ResponseHolder>
    {

        //ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("please wait...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ResponseHolder doInBackground(Integer... params)
        {
            int buttonId = params[0];
            responseHolder.actionId = buttonId;
            return postByAction(buttonId);
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
//	private void setButtonByAction(int actionId)
//	{
//		boolean statusButton = false;
//		switch (actionId)
//		{
//		case R.id.get_upstream_button:
//			statusButton = true;
//			break;
//
//		case R.id.send_file_button:
//			statusButton = false;
//			break;
//
//		default:
//			break;
//		}
//		sendFileButton.setEnabled(statusButton);
//	}

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
        String fileLocation = String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        String filePath = fileLocation+"/"+RECORDED_FILE_NAME;
        try {
            raw = new FileInputStream(filePath);
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

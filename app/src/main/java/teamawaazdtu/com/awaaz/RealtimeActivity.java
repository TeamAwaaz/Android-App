package teamawaazdtu.com.awaaz;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;
import omrecorder.WriteAction;

public class RealtimeActivity extends AppCompatActivity {

    ImageButton btnStop, btnSpeak;
    ImageView ivEmotion;
    TextView tvEmotion, tvRecording, tvTime, tvFirstAnalysis;

    //------OM RECORDER----------
    Recorder recorder;
    int i = 0;
    int mic = 0;
    int recording = 0;
    int analysisStarted = 0;
    int time = 0;
    Timer timer1,timer2;

    SharedPreferences preferences;
    public static final String CREATED_TIME = "created_time";
    public static final String RECORDED_FILE_NAME = "_rec.wav";
    private static final String RECORDING_URL = "https://apiv3.beyondverbal.com/v3/recording/";
    private static final String Auth_URL = "https://token.beyondverbal.com/token";
    private static final String APIKey ="67927a9d-e87c-41cc-8bc1-ec3d3af5acf7";
    private Header access_token;
    private ResponseHolder responseHolder;
    private String recordingid ;
    private TextView tvStatusContent;
    private TextView tvResponseContent;
    String currentPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_realtime);
        relativeLayout.getBackground().setAlpha(30);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_realtime_display);
        linearLayout.getBackground().setAlpha(250);

        bindViews();
        setListeners();

        time = 0;
        i = 0;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onBackPressed() {
        if(recording==1) {
            btnStop.performClick();
        }
        super.onBackPressed();
    }

    private void bindViews() {
        btnSpeak = (ImageButton) findViewById(R.id.btn_speak_realtime);
        btnStop = (ImageButton) findViewById(R.id.btn_stop);
        ivEmotion = (ImageView) findViewById(R.id.iv_emotion);
        tvRecording = (TextView) findViewById(R.id.tv_recording_title);
        tvEmotion = (TextView) findViewById(R.id.tv_emotion);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvFirstAnalysis = (TextView) findViewById(R.id.tv_first_analysis);
        tvTime.setAlpha(0);

        //--------------------
        responseHolder = new ResponseHolder();
        tvResponseContent = (TextView) findViewById(R.id.tv_response_content);
        tvStatusContent = (TextView) findViewById(R.id.tv_status_content);
    }

    private void setListeners() {
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(recording!=1) {
                    setupRecorder();
                    recorder.startRecording();
                    Toast.makeText(RealtimeActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                    recording = 1;
                    time = 0;
                    updateRecTextView(recording);
                    recordingsBreakAndAnalyze();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (recorder != null) {
                        recorder.stopRecording();
                        recording = 0;
                        updateRecTextView(recording);
                    } else {
//                        Toast.makeText(RealtimeActivity.this, "Already Stopped", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                btnSpeak.post(new Runnable() {
                    @Override public void run() {
                        animateVoice(0);
                        changeColor(0);
                    }
                });

                tvFirstAnalysis.animate().alpha(1).setDuration(500);

            }
        });
    }

    private void recordingsBreakAndAnalyze() {
        handleTimer();
        handleBreaking();
    }

    private void handleTimer() {
        int timeDelay = 1000;
        int timePeriod = 1000;

        if(timer2!=null) {
            timer2.cancel();
            timer2 = null;
            time = 0;
        }
        timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (recording == 1) {
                    time++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTime.setText(String.valueOf(time));
                        }
                    });
                }
                else{
                    timer2.cancel();
                    time = 0;
                }
            }
        },timeDelay,timePeriod);

    }

    private void handleBreaking() {
        int delay = 15000;
        int period = 15000;
        if(timer1!=null) {
            timer1.cancel();
            timer1 = null;
        }
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if(recording==1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cutRecordingAndProcess();
                        }
                    });
                }
                else{
                    timer1.cancel();
                }
            }
        },delay,period);
    }

    private void cutRecordingAndProcess() {
        try {
            if(recorder!=null) {
                recorder.stopRecording();
                currentPath = recentFile();
                processRecentFile();
//                Log.d("recentFileFirst",recentFile());
                speakAgain();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            Log.d("cutError: ",e+"");
            e.printStackTrace();
        }
    }

    private void speakAgain() {
        setupRecorder();
        recorder.startRecording();
    }

    private void processRecentFile() {
        hitServer();
    }

    private void hitServer() {
        MediaPlayer mediaPlayer = new MediaPlayer();
//        FileInputStream fis = null;
        try {

            final double[] duration = new double[1];
//            Log.d("LastAudioAnalysis",filePath);
            Log.d("RecentFile: ",recentFile());
//            fis = new FileInputStream(new File(recentFile()));
            mediaPlayer.setDataSource(currentPath);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    duration[0] = mediaPlayer.getDuration()/1000.0;
//                    Log.d("TAG123",duration[0]+"");
                    if(duration[0] >10) {
                        new ServerConnection().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        mediaPlayer.start();
                    }
                    else{
                        Toast.makeText(RealtimeActivity.this, "Audio file should be of more than 10 seconds.", Toast.LENGTH_SHORT).show();

                        File file = new File(currentPath);
                        file.delete();
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
            super.onPreExecute();
        }

//        @Override
//        protected ResponseHolder doInBackground(Integer... params)
//        {
////            int buttonId = params[0];
////            responseHolder.actionId = buttonId;
//            return postByAction();
//        }

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

            super.onPostExecute(responseHolder);
        }
    }

    private ResponseHolder postByAction()
    {
        //System.setProperty("http.proxyHost", "127.0.0.1");
        //System.setProperty("http.proxyPort", "8888");

        HttpActivity httpa = new HttpActivity();

//        switch (buttonId)
//        {
//            case R.id.btn_analyze:   // add btn
//                getToken();
//                responseHolder = httpa.doPost(RECORDING_URL + "start", access_token, getEntityForUpstream());
//
//                if (responseHolder.content != null){
//                    recordingid = getRecordingid(responseHolder.content);
//                    GoStream();
//
//                }
//                break;
//            case R.id.btn_send:  //add btn
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
//
////        tvWait.post(new Runnable() {
////            public void run() {
////                tvWait.setText("Wait");   //change
////            }
////        });
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
//                        tvResponseContent.setText("Full analysis::::"+hol.content+text);   //change
//
//                        updateAnalysisUI(hol.content);
//                        Log.d("TAG123","Full analysis::::"+hol.content);
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
////        Analyze();
//    }
//
//    private long FromMs = 0;

//    public void Analyze(){
//
//        final Timer myTimer = new Timer();
//        long delay = 0;
//        long period=5000;
//        //final Handler uiHandler = new Handler();
//
//        myTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                HttpActivity httpac = new HttpActivity();
//                try{
//
////                    tvWait.post(new Runnable() {
////                        public void run() {
////                            tvWait.append(".");
////                        }
////                    });
//
//                    final ResponseHolder hol = httpac.doGet(RECORDING_URL + recordingid + "/analysis?fromMs=" + FromMs, access_token);
//
//                    String status = getJName(hol.content, "status");
//                    String sesionStatus = getsesionStatus(hol.content);
//                    String f = getDuration(hol.content);
//                    if (status.equals("success")) {
//                        FromMs = Long.parseLong(f.replace(".0", ""));
//                    }
//                    if (sesionStatus!=null && sesionStatus.equals("Done")) {
//                        myTimer.cancel();
////                        tvWait.post(new Runnable() {
////                            public void run() {
////                                tvWait.setText("");
////                            }
////                        });
//                    }
//
//                    tvResponseContent.post(new Runnable() {
//                        public void run() {
////							CharSequence text = responseContentTextView.getText();
////							responseContentTextView.setText("\n....\n"+hol.content+text);
//                            tvResponseContent.append("\n....\n"+hol.content);
//                        }
//                    });
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//        },delay,period);
//
//
//    }

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

    private void updateAnalysisUI(String content) {

        if(analysisStarted==0){
            analysisStarted = 1;
            tvFirstAnalysis.animate().alpha(0).setDuration(500);
        }

        final String emotionText = getEmotionText(content);
        Log.d("EmotionText", emotionText+"");

        ivEmotion.animate().alpha(0).setDuration(500);
        tvEmotion.animate().alpha(0).setDuration(500);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivEmotion.setImageResource(R.drawable.case1emotion);
                if(emotionText!=null) {
                    tvEmotion.setText(emotionText);
                }
                else{
                    tvEmotion.setText("Almost silent... Try again");
                }
                ivEmotion.animate().alpha(1).setDuration(500);
                tvEmotion.animate().alpha(1).setDuration(500);
            }
        },500);
        // to do
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
//            Toast.makeText(this, "Some problem at Server-end", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return emotionText;
    }

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
//        String createdTime = preferences.getString(CREATED_TIME,"invalid");
//        String fileLocation = String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC));
//        String filePath = fileLocation+"/"+ createdTime + RECORDED_FILE_NAME;
        try {
            Log.d("recentFileGetEntity: ", currentPath);
            raw = new FileInputStream(currentPath);
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


    //-------------------------------------------------------------------------------------------


    private void updateRecTextView(int recording) {
        if(recording==1){
            tvTime.setAlpha(1);
            tvRecording.setText("Analyzing...");
            tvTime.setText("");
        }
        else{
            tvRecording.setText("Start Recording");
            tvTime.setAlpha(0);
            time = 0;
        }
    }

    //--------------------OM RECORDER--------------------

    private void setupRecorder() {

        new Runnable() {
            @Override
            public void run() {
                if (recorder != null) {
                    recorder = null;
                }
                recorder = OmRecorder.wav(
                        new PullTransport.Noise(mic(), new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                                changeColor(1);
                            }
                        },new WriteAction.Default(),
                        new Recorder.OnSilenceListener() {
                            @Override public void onSilence(long silenceTime) {
//                                        Log.e("silenceTime", String.valueOf(silenceTime));
//                                        Toast.makeText(RealtimeActivity.this, "silence of " + silenceTime + " detected",
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

    private void changeColor(final int status) {


        if (status == 0) {
            btnSpeak.setImageResource(R.drawable.ic_mic2);
        } else {
            if (i < 10) {
                i++;
//                btnSpeak.setImageResource(R.drawable.ic_mic1);
            } else {
                i = 0;
                if (mic == 0) {
                    mic = 1;
                    btnSpeak.setImageResource(R.drawable.ic_mic1);
                } else {
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
        new Runnable() {
            @Override
            public void run() {
                btnSpeak.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
            }
        }.run();
    }


    @NonNull
    private File recordedFile() {
        Log.d("recordedFile", "called");
        Date createdTimeWithSpaces = new Date();
        String createdTime = "";
        int count = 0;
        for (int i = 0; i < (createdTimeWithSpaces.toString()).length(); i++) {
            if (count == 14) {
                break;
            }
            char curr = createdTimeWithSpaces.toString().charAt(i);
            if (curr != ' ' && curr != ':' && curr != '+') {
                createdTime += curr;
                count++;
            }
        }
        String fileLocation = String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        String filePath = fileLocation + "/" + createdTime + RECORDED_FILE_NAME;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CREATED_TIME, createdTime.toString());
        editor.commit();
//        preferences.edit().putString(CREATED_TIME,createdTime.toString());
        String createdTimeString = preferences.getString(CREATED_TIME, "invalid");
        Log.d("LastAudioWhenRecorded", createdTimeString);
        return new File(filePath);
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
}

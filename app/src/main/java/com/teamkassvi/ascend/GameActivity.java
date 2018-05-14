package com.teamkassvi.ascend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private Uri mUriPhotoTaken;
    private static final int REQUEST_TAKE_PHOTO = 0;
    ProgressDialog mProgressDialog;

    ArrayList<String> emotionNamesList;
    ArrayList<Integer> emotionImagesList;
    int mode = 0;
    int score = 0;
    TextView tvScore, tvEmoName;
    ImageView ivEmoPic,ivMyPic;

    //mode 0 = happy, mode 1 = sad, mode 2 = angry, mode 3 = surprise

    // The image selected to detect.
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_game);
        relativeLayout.getBackground().setAlpha(30);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.progress_dialog_title));
        tvScore = findViewById(R.id.tv_result);
        tvEmoName = findViewById(R.id.tv_emo_name);
        ivEmoPic = findViewById(R.id.iv_emo_pic);
        ivMyPic = findViewById(R.id.iv_my_pic);

        emotionNamesList = new ArrayList<>();
        emotionImagesList = new ArrayList<>();

        emotionNamesList.add("HAPPY");
        emotionNamesList.add("NEUTRAL");
        emotionNamesList.add("SURPRISE");
        emotionNamesList.add("SAD");
        emotionNamesList.add("ANGER");

        emotionImagesList.add(R.drawable.img_em_happy);
        emotionImagesList.add(R.drawable.img_em_neutral);
        emotionImagesList.add(R.drawable.img_em_surprise);
        emotionImagesList.add(R.drawable.img_em_sad);
        emotionImagesList.add(R.drawable.img_em_angry);

        initializeMode();
    }

    public void initializeMode(){
        mode = 0;
        tvEmoName.setText(emotionNamesList.get(0));
        ivEmoPic.setImageResource(emotionImagesList.get(0));
    }

    public void onClickNext(View v){
        mode++;
        if(mode==5){
            mode = 0;
        }

        tvEmoName.setText(emotionNamesList.get(mode));
        ivEmoPic.setImageResource(emotionImagesList.get(mode));
        ivMyPic.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ImageUri", mUriPhotoTaken);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUriPhotoTaken = savedInstanceState.getParcelable("ImageUri");
    }

    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        private boolean mSucceed = true;

        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            if(faceServiceClient==null){
                Log.d("GameErr", "null");
            }
            try {
                publishProgress("Detecting...");

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        true,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        new FaceServiceClient.FaceAttributeType[] {
                                FaceServiceClient.FaceAttributeType.Age,
                                FaceServiceClient.FaceAttributeType.Gender,
                                FaceServiceClient.FaceAttributeType.Smile,
                                FaceServiceClient.FaceAttributeType.Glasses,
                                FaceServiceClient.FaceAttributeType.FacialHair,
                                FaceServiceClient.FaceAttributeType.Emotion,
                                FaceServiceClient.FaceAttributeType.HeadPose,
                                FaceServiceClient.FaceAttributeType.Accessories,
                                FaceServiceClient.FaceAttributeType.Blur,
                                FaceServiceClient.FaceAttributeType.Exposure,
                                FaceServiceClient.FaceAttributeType.Hair,
                                FaceServiceClient.FaceAttributeType.Makeup,
                                FaceServiceClient.FaceAttributeType.Noise,
                                FaceServiceClient.FaceAttributeType.Occlusion
                        });
            } catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
//                addLog(e.getMessage());
                return null;
            }
        }

        private void customToast(String message, int duration) {

            LayoutInflater inflater = getLayoutInflater();
            View customView = inflater.inflate(R.layout.dialog_custom_toast, null);

            TextView tvMsg = customView.findViewById(R.id.tv_msg);
            tvMsg.setText(message+"");
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(duration);
            toast.setView(customView);

            toast.show();

        }

        private void setUiAfterDetection(Face[] result, boolean succeed) {
            mProgressDialog.dismiss();

            if (succeed) {
                // The information about the detection result.
                String detectionResult;
                if (result != null) {
                    detectionResult = result.length + " face"
                            + (result.length != 1 ? "s" : "") + " detected";

                    // Show the detected faces on original image.
                    ImageView ivMyPic = (ImageView) findViewById(R.id.iv_my_pic);
                    ivMyPic.setImageBitmap(ImageHelper.drawFaceRectanglesOnBitmap(
                            mBitmap, result, true));

                    String emotionDetected = getEmotion(result[0].faceAttributes.emotion);

                    if(emotionDetected.equals(emotionNamesList.get(mode))){
                        customToast("+10 (Correct)",Toast.LENGTH_SHORT);
                        score+=10;
                    }
                    else{
                        customToast("-5 (Wrong)",Toast.LENGTH_SHORT);
                        score-=5;
                    }

                    tvScore.setText("Score : " + score);
                    Log.d("GAMERES", emotionDetected);
                }
            }
//                 else {
//                    detectionResult = "0 face detected";
//                }
//                setInfo(detectionResult);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
//            addLog("Request: Detecting in image " + mImageUri);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setMessage(progress[0]);
//            setInfo(progress[0]);
        }

        @Override
        protected void onPostExecute(Face[] result) {
            if (mSucceed) {
//                addLog("Response: Success. Detected " + (result == null ? 0 : result.length)
//                        + " face(s) in " + mImageUri);
            }

            // Show the result on screen when detection is done.
            setUiAfterDetection(result, mSucceed);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    Uri imageUri;
                    if (data == null || data.getData() == null) {
                        imageUri = mUriPhotoTaken;
                    } else {
                        imageUri = data.getData();
                    }
                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            imageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView ivMyPic = findViewById(R.id.iv_my_pic);
                        ivMyPic.setImageBitmap(mBitmap);

                        checkAnswer();
                        // Add detection log.
//                        addLog("Image: " + mImageUri + " resized to " + mBitmap.getWidth()
//                                + "x" + mBitmap.getHeight());
                    }

                    // Clear the detection result.
//                    FaceListAdapter faceListAdapter = new FaceListAdapter(null);
//                    ListView listView = (ListView) findViewById(R.id.list_detected_faces);
//                    listView.setAdapter(faceListAdapter);

                    // Clear the information panel.
//                    setInfo("");

                    // Enable button "detect" as the image is selected and not detected.
//                    setDetectButtonEnabledStatus(true);
                }
                break;
            default:
                break;
        }
    }

    private String getEmotion(Emotion emotion)
    {
        String emotionType = "";
        double emotionValue = 0.0;
        if (emotion.anger > emotionValue)
        {
            emotionValue = emotion.anger;
            emotionType = "ANGER";
        }
        if (emotion.contempt > emotionValue)
        {
            emotionValue = emotion.contempt;
            emotionType = "Contempt";
        }
        if (emotion.disgust > emotionValue)
        {
            emotionValue = emotion.disgust;
            emotionType = "Disgust";
        }
        if (emotion.fear > emotionValue)
        {
            emotionValue = emotion.fear;
            emotionType = "Fear";
        }
        if (emotion.happiness > emotionValue)
        {
            emotionValue = emotion.happiness;
            emotionType = "HAPPY";
        }
        if (emotion.neutral > emotionValue)
        {
            emotionValue = emotion.neutral;
            emotionType = "NEUTRAL";
        }
        if (emotion.sadness > emotionValue)
        {
            emotionValue = emotion.sadness;
            emotionType = "SAD";
        }
        if (emotion.surprise > emotionValue)
        {
            emotionValue = emotion.surprise;
            emotionType = "SURPRISE";
        }

        return String.format("%s",emotionType);
//        return String.format("%s: %f", emotionType, emotionValue);
    }

    public void checkAnswer() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // Start a background task to detect faces in the image.

        if(inputStream!=null) {
            new DetectionTask().execute(inputStream);
        }

        // Prevent button click during detecting.
//        setAllButtonsEnabledStatus(false);
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null) {
            // Save the photo taken to a temporary file.
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File file = File.createTempFile("IMG_", ".jpg", storageDir);
                mUriPhotoTaken = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                } else {
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                }
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                Log.d("GameExc : ", e + "");
            }
        }
    }
}

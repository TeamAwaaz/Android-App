package com.teamkassvi.ascend;

import android.app.Application;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

/**
 * Created by kartik1 on 17-04-2018.
 */

public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        sFaceServiceClient = new FaceServiceRestClient(getString(R.string.endpoint), getString(R.string.subscription_key));
    }

    public static FaceServiceClient getFaceServiceClient() {
        return new FaceServiceRestClient("https://westcentralus.api.cognitive.microsoft.com/face/v1.0", "c467e48cfe3e418da2643039f68c4dcd");
    }

    private static FaceServiceClient sFaceServiceClient;
}

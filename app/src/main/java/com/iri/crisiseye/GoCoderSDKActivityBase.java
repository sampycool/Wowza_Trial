/**
 *  This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be a reference for the
 *  purpose of educating developers, and is not intended to be used in any production environment.
 *
 *  IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 *  OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 *  EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 *  WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *  Copyright © 2015 Wowza Media Systems, LLC. All rights reserved.
 */

package com.iri.crisiseye;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Config;
import android.view.View;
import android.view.WindowManager;

import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WZMediaConfig;
import com.wowza.gocoder.sdk.api.configuration.WowzaConfig;
import com.wowza.gocoder.sdk.api.devices.WZCameraView;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.logging.WZLog;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

import com.iri.crisiseye.config.ConfigPrefs;

import java.util.Arrays;

public abstract class GoCoderSDKActivityBase extends Activity
    implements WZStatusCallback {

    private final static String TAG = GoCoderSDKActivityBase.class.getSimpleName();

    //private static final String SDK_SAMPLE_APP_LICENSE_KEY = "GOSK-F543-0103-4819-FC11-78BE";
    //"";GOSK-5442-0101-750D-4A14-FB5C
    // GOSK-B043-0103-BE47-A06C-205E
        //GOSK-CF43-0103-90F8-05AB-710C
    private static final String SDK_SAMPLE_APP_LICENSE_KEY = "GOSK-F743-0100-F673-E75A-340A";

    private static final int PERMISSIONS_REQUEST_CODE = 0x1;

    protected String[] mRequiredPermissions = {};

    private static Object sBroadcastLock = new Object();
    private static boolean sBroadcastEnded = true;

    // indicates whether this is a full screen activity or not
    protected static boolean sFullScreenActivity = true;

    // GoCoder SDK top level interface
    protected static WowzaGoCoder sGoCoderSDK = null;

    /**
     * Build an array of WZMediaConfigs from the frame sizes supported by the active camera
     * @param goCoderCameraView the camera view
     * @return an array of WZMediaConfigs from the frame sizes supported by the active camera
     */
    protected static WZMediaConfig[] getVideoConfigs(WZCameraView goCoderCameraView) {
        WZMediaConfig configs[] = WowzaConfig.PRESET_CONFIGS;

        if (goCoderCameraView != null && goCoderCameraView.getCamera() != null) {
            WZMediaConfig cameraConfigs[] = goCoderCameraView.getCamera().getSupportedConfigs();
            Arrays.sort(cameraConfigs);
            configs = cameraConfigs;
        }

        return configs;
    }

    protected boolean mPermissionsGranted = false;

    protected WZBroadcast mWZBroadcast = null;
    public WZBroadcast getBroadcast() {
        return mWZBroadcast;
    }

    protected WZBroadcastConfig mWZBroadcastConfig = null;
    public WZBroadcastConfig getBroadcastConfig() {

        return mWZBroadcastConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (sGoCoderSDK == null) {
            // Enable detailed logging from the GoCoder SDK
            WZLog.LOGGING_ENABLED = true;

            // Initialize the GoCoder SDK
            sGoCoderSDK = WowzaGoCoder.init(this, SDK_SAMPLE_APP_LICENSE_KEY);
            System.out.println(" >> == WowzaGoCoder initialised ");
            if (sGoCoderSDK == null) {
                WZLog.error(TAG, WowzaGoCoder.getLastError());
                System.out.println(" >> == WowzaGoCoder failed to initialise");
            }
        }

        if (sGoCoderSDK != null) {
            // Create a GoCoder broadcaster and an associated broadcast configuration
            mWZBroadcast = new WZBroadcast();
            System.out.println(" >> == WZBroadcast initialised ");
            mWZBroadcastConfig = new WZBroadcastConfig(sGoCoderSDK.getConfig());
            System.out.println(" >> == mWZBroadcastConfig initialised ");
            mWZBroadcastConfig.setLogLevel(WZLog.LOG_LEVEL_DEBUG);


            // added by me
//            mWZBroadcastConfig.setHostAddress("rtsp://de243b.entrypoint.cloud.wowza.com/app-d212");

//            mWZBroadcastConfig.setHostAddress("de243b.entrypoint.cloud.wowza.com/app-d212");
            mWZBroadcastConfig.setHostAddress("rtmp://a048b8.entrypoint.cloud.wowza.com/app-60f9");
            mWZBroadcastConfig.setPortNumber(1935);
            mWZBroadcastConfig.setApplicationName("live");
//            mWZBroadcastConfig.setStreamName("Trial CVDI US");
            mWZBroadcastConfig.setStreamName("Ttestsss");


        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp =  PreferenceManager.getDefaultSharedPreferences(GoCoderSDKActivityBase.this);

        System.out.println("wz_live_host_address >> == <<"+ sp.getString("wz_live_host_address", null));

        if (mWZBroadcast != null) {
            mPermissionsGranted = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mPermissionsGranted = (mRequiredPermissions.length > 0 ? WowzaGoCoder.hasPermissions(this, mRequiredPermissions) : true);
                if (!mPermissionsGranted)
                    ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
            }

            if (mPermissionsGranted) {
                ConfigPrefs.updateConfigFromPrefs(PreferenceManager.getDefaultSharedPreferences(this), mWZBroadcastConfig);
//                ConfigPrefs.
//                brrr

             System.out.println(" > " + mWZBroadcastConfig.getVideoFrameHeight());
             System.out.println(" > "+   mWZBroadcastConfig.getVideoKeyFrameInterval());
             System.out.println(" > " +    mWZBroadcastConfig.getVideoFrameWidth());
             System.out.println(" > " + mWZBroadcastConfig.getVideoFramerate());
             System.out.println(" > " + mWZBroadcastConfig.getVideoFrameSize());

            }
        }
    }

    @Override
    protected void onPause() {
        // Stop any active live stream
        if (mWZBroadcast != null && mWZBroadcast.getStatus().isRunning()) {
            endBroadcast(true);
        }

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        mPermissionsGranted = true;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                for(int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionsGranted = false;
                    }
                }
            }
        }
    }

    /**
     * Enable Android's sticky immersive full-screen mode
     * See http://developer.android.com/training/system-ui/immersive.html#sticky
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (sFullScreenActivity && hasFocus) {
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            if (rootView != null)
                rootView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    /**
     * WZStatusCallback interface methods
     */
    @Override
    public void onWZStatus(final WZStatus goCoderStatus) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (goCoderStatus.isReady()) {
                    // Keep the screen on while the broadcast is active
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    // Since we have successfully opened up the server connection, store the connection info for auto complete
                    ConfigPrefs.storeAutoCompleteHostConfig(PreferenceManager.getDefaultSharedPreferences(GoCoderSDKActivityBase.this), mWZBroadcastConfig);
//                    brrrr

                } else if (goCoderStatus.isIdle())
                    // Clear the "keep screen on" flag
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                WZLog.debug(TAG, goCoderStatus.toString());
            }
        });
    }

    @Override
    public void onWZError(final WZStatus goCoderStatus) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                WZLog.error(TAG, goCoderStatus.getLastError());
            }
        });
    }

    public synchronized  void changeBroadCastParams(int bitRate_str, int framerate_str, int keyframe_interval_str)
    {
        System.out.println(">> ]] << in changeBroadCastParams method");
        mWZBroadcastConfig.setVideoBitRate(bitRate_str);
        mWZBroadcastConfig.setVideoFramerate(framerate_str);
        mWZBroadcastConfig.setVideoKeyFrameInterval(keyframe_interval_str);
    }

    protected synchronized WZStreamingError startBroadcast() {
        WZStreamingError configValidationError = null;
        System.out.println(">> == << in startBroadcast method");
        System.out.println(" >> " + mWZBroadcastConfig.getVideoFrameHeight());
        System.out.println(" >> ]] keyframe_interval_str "+   mWZBroadcastConfig.getVideoKeyFrameInterval());
        System.out.println(" >> " +    mWZBroadcastConfig.getVideoFrameWidth());
        System.out.println(" >> ]] framerate " + mWZBroadcastConfig.getVideoFramerate());
        System.out.println(" >> " + mWZBroadcastConfig.getVideoFrameSize());
        System.out.println(" >> " + mWZBroadcastConfig.getFrameBufferSizeMultiplier());
        System.out.println(" >> ]] bitrate " + mWZBroadcastConfig.getVideoBitRate());

        System.out.println(" >> ]] " + mWZBroadcastConfig.getLowBandwidthScalingFactor());
        System.out.println(" >> ]] " + mWZBroadcastConfig.getFrameRateLowBandwidthSkipCount());


        if (mWZBroadcast.getStatus().isIdle()) {
            WZLog.info(TAG, "=============== Broadcast Configuration ===============\n"
                    + mWZBroadcastConfig.toString()
                        + "\n=======================================================");
//            mWZBroadcastConfig.setHostAddress("de243b.entrypoint.cloud.wowza.com/app-d212");
//            mWZBroadcastConfig.setHostAddress("https://player.cloud.wowza.com/hosted/vjfvh3qg/");
//            mWZBroadcastConfig.setPortNumber(1935);
//            mWZBroadcastConfig.setApplicationName("live");
//            mWZBroadcastConfig.setStreamName("d3a730b3");
            System.out.println(">> == << Broadcast Configuration 1 " + mWZBroadcastConfig.getApplicationName());
            System.out.println(">> == << Broadcast Configuration 1 " + mWZBroadcastConfig.getPortNumber());
            System.out.println(">> == << Broadcast Configuration 1 " + mWZBroadcastConfig.getHostAddress());
            System.out.println(">> == << Broadcast Configuration 1 " + mWZBroadcastConfig.getStreamName());
//            mWZBroadcastConfig.setHostAddress("de243b.entrypoint.cloud.wowza.com/app-d212");
//            mWZBroadcastConfig.setHostAddress("rtmp://a048b8.entrypoint.cloud.wowza.com/app-60f9");

//            192.168.100.7:1935/app-60f9
//            mWZBroadcastConfig.setHostAddress("a048b8.entrypoint.cloud.wowza.com");
//            mWZBroadcastConfig.setHostAddress("192.168.1.106");
//            mWZBroadcastConfig.setHostAddress("192.168.100.5");
//            mWZBroadcastConfig.setHostAddress("10.230.249.189");
//            mWZBroadcastConfig.setHostAddress("10.231.243.14");
//            mWZBroadcastConfig.setHostAddress("a048b8.entrypoint.cloud.wowza.com");
//            mWZBroadcastConfig.setHostAddress("10.50.62.95");
//            mWZBroadcastConfig.setPortNumber(1935);



//            mWZBroadcastConfig.setApplicationName("app-60f9"); // change to app-60f9
//            mWZBroadcastConfig.setStreamName("Trial CVDI US");
//            mWZBroadcastConfig.setStreamName("testsss");

            //for streaming engine
//            mWZBroadcastConfig.setApplicationName("tech_mech"); // change to
//            mWZBroadcastConfig.setStreamName("myStream");
//            mWZBroadcastConfig.setUsername("crisiseye-app");
//            mWZBroadcastConfig.setPassword("r22");


            // For streaming cloud PUSH
            /*mWZBroadcastConfig.setApplicationName("app-60f9"); // change to
            mWZBroadcastConfig.setStreamName("28a98947");
            mWZBroadcastConfig.setUsername("client21702");
            mWZBroadcastConfig.setHostAddress("a048b8.entrypoint.cloud.wowza.com");
            mWZBroadcastConfig.setPassword("sampycool007");
            mWZBroadcastConfig.setPortNumber(1935);*/

            //for streaming engine
            /*mWZBroadcastConfig.setHostAddress("10.230.249.189");
            mWZBroadcastConfig.setApplicationName("test2"); // change to
            mWZBroadcastConfig.setStreamName("myStream");
            mWZBroadcastConfig.setUsername("twisampati");
            mWZBroadcastConfig.setPassword("sampycool007");
            mWZBroadcastConfig.setPortNumber(1935);*/


           /* mWZBroadcastConfig.setHostAddress("192.168.80.181:8080");
            mWZBroadcastConfig.setApplicationName("test2"); // change to
            mWZBroadcastConfig.setStreamName("myStream");
            mWZBroadcastConfig.setUsername("twisampati");
            mWZBroadcastConfig.setPassword("sampycool007");
            mWZBroadcastConfig.setPortNumber(1935);*/



           /* System.out.println(">> == << Broadcast Configuration 2 " + mWZBroadcastConfig.getApplicationName());
            System.out.println(">> == << Broadcast Configuration 2 " + mWZBroadcastConfig.getPortNumber());
            System.out.println(">> == << Broadcast Configuration 2 " + mWZBroadcastConfig.getHostAddress());
            System.out.println(">> == << Broadcast Configuration 2 " + mWZBroadcastConfig.getStreamName());*/
            configValidationError = mWZBroadcastConfig.validateForBroadcast();
            if (configValidationError == null) {
                System.out.println(">> == << in configError null condition >> ");
                System.out.println(">> == << going to start broadcast");
                mWZBroadcast.startBroadcast(mWZBroadcastConfig, this);
            }
        } else {
            System.out.println(">> == << in configError null condition else condition >> ");
            System.out.println("startBroadcast() called while another broadcast is active");
            WZLog.error(TAG, "startBroadcast() called while another broadcast is active");
        }
        return configValidationError;
    }

    protected synchronized void endBroadcast(boolean appPausing) {
        if (!mWZBroadcast.getStatus().isIdle()) {
            if (appPausing) {
                // Stop any active live stream
                sBroadcastEnded = false;
                mWZBroadcast.endBroadcast(new WZStatusCallback() {
                    @Override
                    public void onWZStatus(WZStatus wzStatus) {
                        synchronized (sBroadcastLock) {
                            sBroadcastEnded = true;
                            sBroadcastLock.notifyAll();
                        }
                    }

                    @Override
                    public void onWZError(WZStatus wzStatus) {
                        WZLog.error(TAG, wzStatus.getLastError());
                        synchronized (sBroadcastLock) {
                            sBroadcastEnded = true;
                            sBroadcastLock.notifyAll();
                        }
                    }
                });

                while(!sBroadcastEnded) {
                    try{
                        sBroadcastLock.wait();
                    } catch (InterruptedException e) {}
                }
            } else {
                mWZBroadcast.endBroadcast(this);
            }
        }  else {
            WZLog.error(TAG, "endBroadcast() called without an active broadcast");
        }
    }

    protected synchronized void endBroadcast() {
        endBroadcast(false);
    }




}

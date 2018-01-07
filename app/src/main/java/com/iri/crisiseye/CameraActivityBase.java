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

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

//import com.trial.cvdi.services.SensorService;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WZMediaConfig;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.devices.WZCamera;
import com.wowza.gocoder.sdk.api.devices.WZCameraView;
import com.wowza.gocoder.sdk.api.encoder.WZEncoderAPI;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.geometry.WZSize;
import com.wowza.gocoder.sdk.api.graphics.WZColor;
import com.wowza.gocoder.sdk.api.h264.WZProfileLevel;
import com.wowza.gocoder.sdk.api.status.WZState;
import com.wowza.gocoder.sdk.api.status.WZStatus;

import com.iri.crisiseye.config.ConfigPrefs;
import com.iri.crisiseye.config.ConfigPrefsActivity;
import com.iri.crisiseye.ui.MultiStateButton;
import com.iri.crisiseye.ui.StatusView;

import java.util.Arrays;

abstract public class CameraActivityBase extends GoCoderSDKActivityBase
//    implements WZStatusCallback {
        implements WZCameraView.PreviewStatusListener{

    private final static String TAG = CameraActivityBase.class.getSimpleName();
    private boolean binded = false;
    public static  int i = 0 ;
    public static boolean updated_values = false;
    public static String framerate_str = "";
    public static String bitRate_str = "";
    public static String keyframe_interval_str = "";


    public boolean isUpdated_values() {
        return updated_values;
    }

    public void setUpdated_values(boolean updated_values) {
        this.updated_values = updated_values;
    }

    public String getFrame_rate() {
        return framerate_str;
    }

    public void setFrame_rate(String frame_rate) {
        this.framerate_str = frame_rate;
    }

    public String getBitRate_str() {
        return bitRate_str;
    }

    public void setBitRate_str(String bitRate_str) {
        this.bitRate_str = bitRate_str;
    }

    public String getKeyframe_interval_str() {
        return keyframe_interval_str;
    }

    public void setKeyframe_interval_str(String keyframe_interval_str) {
        this.keyframe_interval_str = keyframe_interval_str;
    }



    // UI controls
    protected MultiStateButton mBtnBroadcast = null;
    protected MultiStateButton mBtnSettings  = null;
    protected StatusView mStatusView   = null;

    // The GoCoder SDK camera preview display view
    protected WZCameraView  mWZCameraView  = null;
    protected WZAudioDevice mWZAudioDevice = null;

    private boolean mDevicesInitialized = false;
    private boolean mUIInitialized      = false;

    public WZBroadcast goCoderBroadcaster =null;
    public  WZBroadcastConfig goCoderBroadcastConfig = null;


    Intent mServiceIntent;
//    private SensorService mSensorService;
    Context ctx;
    public Context getCtx()
    {
        return ctx;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        System.out.println(">> == << on create called");
        ctx = this;
        // Designate the camera preview as the video source
//        goCoderBroadcastConfig.setVideoBroadcaster(mWZCameraView);
        mWZBroadcastConfig.setVideoBroadcaster(mWZCameraView);
        // Designate the audio device as the audio broadcaster
//        goCoderBroadcastConfig.setAudioBroadcaster(mWZAudioDevice);
        mWZBroadcastConfig.setAudioBroadcaster(mWZAudioDevice);

    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println(">> == << on resume called");
        if (!mUIInitialized)
            initUIControls();
        if (!mDevicesInitialized)
            initGoCoderDevices();

        if (sGoCoderSDK != null && mPermissionsGranted) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);


            mWZCameraView.setCameraConfig(getBroadcastConfig());   // here we get the settings from the gocodersdkactivitybase
            mWZCameraView.setScaleMode(ConfigPrefs.getScaleMode(sharedPrefs));
            mWZCameraView.setVideoBackgroundColor(WZColor.DARKGREY);

            if (mWZBroadcastConfig.isVideoEnabled()) {
                if (mWZCameraView.isPreviewPaused())
                    mWZCameraView.onResume();
                else
                    mWZCameraView.startPreview();
            }

            // Briefly display the video frame size from config
            Toast.makeText(this, getBroadcastConfig().getLabel(true, true, false, true), Toast.LENGTH_LONG).show();
        }

        syncUIControlState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println(">> == << on pause called");
        if (mWZCameraView != null) {
            mWZCameraView.onPause();
        }
    }


    /**
     * WZStatusCallback interface methods
     */
    @Override
    public void onWZStatus(final WZStatus goCoderStatus) {


        final StringBuffer statusMessage = new StringBuffer("Broadcast status: ");

        switch (goCoderStatus.getState()) {
            case WZState.STARTING:
                statusMessage.append("Broadcast initialization");
                break;

            case WZState.READY:
                statusMessage.append("Ready to begin streaming");
                break;

            case WZState.RUNNING:
                statusMessage.append("Streaming is active");
                break;

            case WZState.STOPPING:
                statusMessage.append("Broadcast shutting down");
                break;

            case WZState.IDLE:
                statusMessage.append("The broadcast is stopped");
                break;

            default:
                return;
        }



        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

//                Toast.makeText(MainActivity.this, statusMessage, Toast.LENGTH_LONG).show();

                if (goCoderStatus.isRunning()) {
                    // Keep the screen on while we are broadcasting
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    // Since we have successfully opened up the server connection, store the connection info for auto complete
                    ConfigPrefs.storeAutoCompleteHostConfig(PreferenceManager.getDefaultSharedPreferences(CameraActivityBase.this), mWZBroadcastConfig);
                } else if (goCoderStatus.isIdle()) {
                    // Clear the "keep screen on" flag
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

                if (mStatusView != null) mStatusView.setStatus(goCoderStatus);
                syncUIControlState();
            }
        });
    }

    @Override
    public void onWZError(final WZStatus goCoderStatus) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mStatusView != null) mStatusView.setStatus(goCoderStatus);
                syncUIControlState();
            }
        });
    }

    /**
     * Click handler for the broadcast button
     */  //android:onClick="onToggleBroadcast"

    public void showVideo(View v)
    {
        System.out.println("// in show video");

    }


    public void onToggleBroadcast(View v) {
        System.out.println("// clicked on toggle broadcast");
        Toast.makeText(this,"clicked on toggle btn",Toast.LENGTH_SHORT).show();


        Runnable brdcast_Runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("]] running thread");
                while(i != 1) {
                    if (updated_values == true) {
                        System.out.println("]] running thread in true condition");
                        System.out.println("]] wz_video_frame_rate" + framerate_str);
                        System.out.println("]] wz_video_bitrate" + bitRate_str);
                        System.out.println("]] wz_video_keyframe_interval" + keyframe_interval_str);
//                   onToggleBroadcast(v);
                        endBroadcast();

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                                (CameraActivityBase.this);
                        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                        prefsEditor.putString("wz_video_frame_rate", framerate_str);
                        prefsEditor.putString("wz_video_bitrate", bitRate_str);
                        prefsEditor.putString("wz_video_keyframe_interval", keyframe_interval_str);
                        prefsEditor.commit();
                        changeBroadCastParams(Integer.parseInt(bitRate_str),Integer.parseInt(framerate_str),
                                Integer.parseInt(keyframe_interval_str));
                        updated_values = false ;
                        System.out.println("]] updating_values "+updated_values);
                        System.out.println("]] starting broadcast");
                        startBroadcast();
                    } else {

                    }
                }
            }
        } ;


        /*ServiceConnection dynamicServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DynamicValue_WebService.LocalDynamicValueBinder binder = (DynamicValue_WebService.LocalDynamicValueBinder) service;
                priorityService = binder.getService();
                binded = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                binded = false;
            }
        };*/

        if (getBroadcast() == null) return;

        if (getBroadcast().getStatus().isIdle()) {
            System.out.println(">> == << in isIdle condition");
            Thread aThread = new Thread(brdcast_Runnable);
            System.out.println(">> :: << starting thread");
            aThread.start();

            WZStreamingError configError = startBroadcast();

            System.out.println(">> == << going to start asynctask");
//            WebService_AsyncTask asyncTask = new WebService_AsyncTask();
//            asyncTask.execute();

//            startService(new Intent(CameraActivityBase.this, DynamicValue_WebService.class));

            /*mSensorService = new SensorService(getCtx());
            mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
            if (!isMyServiceRunning(mSensorService.getClass())) {
                startService(mServiceIntent);
            }*/


//            this.bindService(new Intent(CameraActivityBase.this, DynamicValue_WebService.class), dynamicServiceConnection, Context.BIND_AUTO_CREATE);

            if (configError != null) {
                System.out.println(">> == << in configError not null condition");
                System.out.println(">> == << configError ErrorCode is "+configError.getErrorCode());
                System.out.println(">> == << configError exception is "+configError.getException());
                if (mStatusView != null) mStatusView.setErrorMessage(configError.getErrorDescription());
            }
            System.out.println(">> == << in configError null condition");
        } else {
            System.out.println(">> == << finishing broadcast");
            endBroadcast();
//            stopService(mServiceIntent);
            /*if (binded) {
                // Unbind Service
                System.out.println(">> == << binded true : going to terminate service");
                this.unbindService(dynamicServiceConnection);
                binded = false;
            }*/
        }
    }

    /**
     * Click handler for the settings button
     */
    public void onSettings(View v) {
        System.out.println(" on settings clicked");
        if (sGoCoderSDK == null) return;

        WZMediaConfig configs[] = (mWZCameraView != null ? getVideoConfigs(mWZCameraView) : new WZMediaConfig[0]);

        System.out.println(">< configs.length is >> " + configs.length);
        for(int i=0 ;i<configs.length;i++)
        {
            System.out.println(">< configs  " + i + " is : "+configs[i].getVideoBitRate());
            System.out.println(">< configs  " + i + " height is : "+configs[i].getVideoFrameHeight()
            + " width is : "+ configs[i].getVideoFrameWidth());
            configs[i].setVideoBitRate(30); // changed here
            configs[i].setVideoBitRate(1500);   // default is 1500
        }
        WZProfileLevel avcProfileLevels[] = WZEncoderAPI.getProfileLevels();
        if (avcProfileLevels.length > 1) Arrays.sort(avcProfileLevels);

        Intent intent = new Intent(this, ConfigPrefsActivity.class);
        intent.putExtra(ConfigPrefs.PREFS_TYPE, ConfigPrefs.ALL_PREFS);
        intent.putExtra(ConfigPrefs.VIDEO_CONFIGS, configs);
        intent.putExtra(ConfigPrefs.H264_PROFILE_LEVELS,  avcProfileLevels);
        startActivity(intent);

    }

    protected void initGoCoderDevices() {
        if (sGoCoderSDK != null && mPermissionsGranted) {

            // Initialize the camera preview
            if (mWZCameraView != null) {
                WZCamera availableCameras[] = mWZCameraView.getCameras();
                // Ensure we can access to at least one camera
                if (availableCameras.length > 0) {
                    // Set the video broadcaster in the broadcast config
                    getBroadcastConfig().setVideoBroadcaster(mWZCameraView);
                } else {
                    mStatusView.setErrorMessage("Could not detect or gain access to any cameras on this device");
                    getBroadcastConfig().setVideoEnabled(false);
                }
            } else {
                getBroadcastConfig().setVideoEnabled(false);
            }

            // Initialize the audio input device interface
            mWZAudioDevice = new WZAudioDevice();

            // Set the audio broadcaster in the broadcast config
            getBroadcastConfig().setAudioBroadcaster(mWZAudioDevice);

            mDevicesInitialized = true;
        }
    }

    @Override
    public void onWZCameraPreviewStarted(WZCamera wzCamera, WZSize wzSize, int i) {
    }

    @Override
    public void onWZCameraPreviewStopped(int cameraId) {
    }

    @Override
    public void onWZCameraPreviewError(WZCamera wzCamera, WZError wzError) {
    }

    protected void initUIControls() {
        // Initialize the UI controls
        mBtnBroadcast       = (MultiStateButton) findViewById(com.iri.crisiseye.R.id.ic_broadcast);
        mBtnSettings        = (MultiStateButton) findViewById(com.iri.crisiseye.R.id.ic_settings);
        mStatusView         = (StatusView) findViewById(com.iri.crisiseye.R.id.statusView);

        // The GoCoder SDK camera view
        mWZCameraView = (WZCameraView) findViewById(com.iri.crisiseye.R.id.cameraPreview);
        mWZCameraView.setPreviewReadyListener(this);

        mUIInitialized = true;

        if (sGoCoderSDK == null && mStatusView != null)
            mStatusView.setErrorMessage(WowzaGoCoder.getLastError().getErrorDescription());
    }

    protected boolean syncUIControlState() {
        boolean disableControls = (getBroadcast() == null ||
                !(getBroadcast().getStatus().isIdle() ||
                        getBroadcast().getStatus().isRunning()));
        boolean isStreaming = (getBroadcast() != null && getBroadcast().getStatus().isRunning());

        if (disableControls) {
            if (mBtnBroadcast != null) mBtnBroadcast.setEnabled(false);
            if (mBtnSettings != null) mBtnSettings.setEnabled(false);
        } else {
            if (mBtnBroadcast != null) {
                mBtnBroadcast.setState(isStreaming);
                mBtnBroadcast.setEnabled(true);
            }
            if (mBtnSettings != null)
                mBtnSettings.setEnabled(!isStreaming);
        }

        return disableControls;
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                System.out.println("isMyServiceRunning true +");
                return true;
            }
        }
        System.out.println("isMyServiceRunning false+");
        return false;
    }


    @Override
    protected void onDestroy() {
//        stopService(mServiceIntent);
        System.out.println("MAINACT onDestroy!");
        super.onDestroy();

    }
}

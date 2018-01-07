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

import android.Manifest;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.wowza.gocoder.sdk.api.devices.WZCamera;

import com.iri.crisiseye.ui.AutoFocusListener;
import com.iri.crisiseye.ui.MultiStateButton;
import com.iri.crisiseye.ui.TimerView;

public class CameraActivity extends CameraActivityBase {
    private final static String TAG = CameraActivity.class.getSimpleName();

    // UI controls
    protected MultiStateButton mBtnSwitchCamera  = null;
    protected MultiStateButton      mBtnTorch         = null;
    protected TimerView mTimerView        = null;
//    Toolbar toolbar;





    // Gestures are used to toggle the focus modes
    protected GestureDetectorCompat mAutoFocusDetector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.iri.crisiseye.R.layout.activity_camera);


//        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
//        setActionBar(toolbar);
//        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
//        toolbar_title.setText("CrisisEye");


        mRequiredPermissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };

        // Initialize the UI controls
        mBtnTorch           = (MultiStateButton) findViewById(com.iri.crisiseye.R.id.ic_torch);
        mBtnSwitchCamera    = (MultiStateButton) findViewById(com.iri.crisiseye.R.id.ic_switch_camera);
        mTimerView          = (TimerView) findViewById(com.iri.crisiseye.R.id.txtTimer);

//        sGoCoderSDK = WowzaGoCoder.init(getApplicationContext(), "GOSK-XXXX-XXXX-XXXX-XXXX-XXXX");
//        goCoderBroadcaster = getBroadcast();
//        goCoderBroadcastConfig = getBroadcastConfig();
//        goCoderBroadcastConfig.setHostAddress("rtsp://de243b.entrypoint.cloud.wowza.com/app-d212");
//        goCoderBroadcastConfig.setPortNumber(1935);
//        goCoderBroadcastConfig.setApplicationName("live");
//        goCoderBroadcastConfig.setStreamName("d3a730b3");

        /*// Designate the camera preview as the video source
        goCoderBroadcastConfig.setVideoBroadcaster(mWZCameraView);

        // Designate the audio device as the audio broadcaster
        goCoderBroadcastConfig.setAudioBroadcaster(mWZAudioDevice);*/

        /*Intent intent = getIntent();
        HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra("map");

        String lat = map.get("lat");
        String lon = map.get("lon");
        String device_time = map.get("device_time");
        String battery_level = map.get("battery_level");
        String orientation = map.get("orientation");
        String device_id = map.get("device_id");*/
    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (sGoCoderSDK != null && mWZCameraView != null) {
            if (mAutoFocusDetector == null)
                mAutoFocusDetector = new GestureDetectorCompat(this, new AutoFocusListener(this, mWZCameraView));

            WZCamera activeCamera = mWZCameraView.getCamera();
            if (activeCamera != null && activeCamera.hasCapability(WZCamera.FOCUS_MODE_CONTINUOUS))
                activeCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Click handler for the switch camera button
     */
    public void onSwitchCamera(View v) {
        if (mWZCameraView == null) return;

        mBtnTorch.setState(false);
        mBtnTorch.setEnabled(false);

        WZCamera newCamera = mWZCameraView.switchCamera();
        if (newCamera != null) {
            if (newCamera.hasCapability(WZCamera.FOCUS_MODE_CONTINUOUS))
                newCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);

            boolean hasTorch = newCamera.hasCapability(WZCamera.TORCH);
            if (hasTorch) {
                mBtnTorch.setState(newCamera.isTorchOn());
                mBtnTorch.setEnabled(true);
            }
        }
    }

    /**
     * Click handler for the torch/flashlight button
     */
    public void onToggleTorch(View v) {
        if (mWZCameraView == null) return;

        WZCamera activeCamera = mWZCameraView.getCamera();
        activeCamera.setTorchOn(mBtnTorch.toggleState());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAutoFocusDetector != null)
            mAutoFocusDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Update the state of the UI controls
     */
    @Override
    protected boolean syncUIControlState() {
        boolean disableControls = super.syncUIControlState();
        if (disableControls) {
            mBtnSwitchCamera.setEnabled(false);
            mBtnTorch.setEnabled(false);
        } else {
            boolean isDisplayingVideo = (getBroadcastConfig().isVideoEnabled() && mWZCameraView.getCameras().length > 0);
            boolean isStreaming = getBroadcast().getStatus().isRunning();

            if (isDisplayingVideo) {
//                Toast.makeText(this,"displaying video",Toast.LENGTH_SHORT).show();
                WZCamera activeCamera = mWZCameraView.getCamera();

                boolean hasTorch = (activeCamera != null && activeCamera.hasCapability(WZCamera.TORCH));
                mBtnTorch.setEnabled(hasTorch);
                if (hasTorch) {
                    mBtnTorch.setState(activeCamera.isTorchOn());
                }

                mBtnSwitchCamera.setEnabled(mWZCameraView.getCameras().length > 0);
                //mBtnSwitchCamera.setEnabled(mWZCameraView.isSwitchCameraAvailable());
            } else {
                mBtnSwitchCamera.setEnabled(false);
                mBtnTorch.setEnabled(false);
            }

            if (isStreaming && !mTimerView.isRunning()) {
                mTimerView.startTimer();
            }
            else if (getBroadcast().getStatus().isIdle() && mTimerView.isRunning()) {
                mTimerView.stopTimer();
            }
            else if (!getBroadcast().getStatus().isIdle()  && mTimerView.isRunning()) {  // added
                Toast.makeText(this,"In check condition",Toast.LENGTH_SHORT).show();
                mTimerView.stopTimer();  //added
                Toast.makeText(this,"starting timer again",Toast.LENGTH_SHORT).show(); //added
                mTimerView.startTimer(1000L,mTimerView.getTime()); //added
            } //added
            else if (!isStreaming) {
                mTimerView.setVisibility(View.GONE);
            }
        }
        /*
        01-07 12:53:46.787 10878-15427/com.iri.crisiseye D/WMSTransport: Session state changed from HANDSHAKE_SERVER0_SEND to HANDSHAKE_SERVER0_SENT
        01-07 12:53:46.791 10878-15428/com.iri.crisiseye W/b: a was in an unexpected state. Expected: IDLE, Actual: STARTING
        01-07 12:53:46.791 10878-15426/com.iri.crisiseye E/b: A broadcast component reported the following error during the PREPARE_FOR_BROADCAST transition
        severity                  : ERROR
        error class               : WZStreamingError
        error code                : 33
        description               : A session component was in an invalid transition state
        */

        return disableControls;
    }
}

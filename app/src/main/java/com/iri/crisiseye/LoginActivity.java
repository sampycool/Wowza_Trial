package com.iri.crisiseye;

/**
 * Created by tsarkar on 02/10/17.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.LocationListener;


import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.iri.crisiseye.asynctask.LoginAsyncTask;
import com.iri.crisiseye.asynctask.PermissionResultCallback;
import com.iri.crisiseye.services.SensorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//import static com.iri.crisiseye.services.SensorService.battery_level;


public class LoginActivity extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback,PermissionResultCallback {

//    TextView txtview_forgotpassword;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;


    double latitude  ;
    double longitude ;
    String deviceID = "";
    TextView txtview_signup;
    TextView txtview_login;
    EditText edittext_userID;
    EditText edittext_password;
    String login_response;
    Toolbar toolbar;

    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;


    // list of permissions

    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;

    boolean isPermissionGranted;

    public void setLoginResponse(String response) {
        this.login_response = response;
    }


   private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//            batteryTxt.setText(String.valueOf(level) + "%");
//            batteryTxt.setText(String.valueOf(level) + "%");
//            Toast.makeText(LoginActivity.this, "battery level showing up! "+level , Toast.LENGTH_SHORT).show();
            SensorService.battery_level = String.valueOf(level);

        }
    };

    public void setDeviceId(String deviceID) {


        this.deviceID = deviceID;
        System.out.println("device_id >");
        System.out.println("device_id : "+ deviceID);
        SharedPreferences pref = getSharedPreferences("login_pref",0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("deviceID",deviceID);
        editor.commit();

    }
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

//    private static String convertToHex(byte[] data) {
//        StringBuilder buf = new StringBuilder();
//        for (byte b : data) {
//            int halfbyte = (b >>> 4) & 0x0F;
//            int two_halfs = 0;
//            do {
//                buf.append(
//                        (0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
//                halfbyte = b & 0x0F;
//            } while (two_halfs++ < 1);
//        }
//        return buf.toString();
//    }

//    public static String convert_SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        MessageDigest md = MessageDigest.getInstance("SHA-1");
//        md.update(text.getBytes("iso-8859-1"), 0, text.length());
//        byte[] sha1hash = md.digest();
//        return convertToHex(sha1hash);
//    }



    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }



    /*
    private void showPlayServicesError(int errorCode)
    {
        GoogleApiAvailability.getInstance().showErrorDialogFragment(this, errorCode, 10,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
    }
    */

    @Override
    protected void onResume() {
        super.onResume();
//        showToast("Checking play services");
        checkPlayServices();
    }
    protected void onStop() {

        // 1. disconnecting the client invalidates it.
//        showToast("disconnecting mGoogleApiClient");
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
//                        showToast("Fetching Location...... BINGO");
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
//                        showToast("user cancelled dialog..");
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        permissionUtils=new PermissionUtils(LoginActivity.this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.READ_PHONE_STATE);

        permissionUtils.check_permission(permissions,"Explain here why the app needs permissions",1);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).
                addApi(LocationServices.API).build();
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setActionBar(toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText("CrisisEye");
//        txtview_forgotpassword = (TextView) findViewById(R.id.txtview_forgotpassword);
        txtview_signup = (TextView) findViewById(R.id.txtview_signup);
//        txtview_termsconditions = (TextView) findViewById(R.id.txtview_termsconditions);
        txtview_login = (TextView) findViewById(R.id.txtview_login);
        edittext_userID = (EditText) findViewById(R.id.edittext_userID);
        edittext_password = (EditText) findViewById(R.id.edittext_password);


        if (checkPlayServices()) {
//            showToast("Fetching Location..");

//            showToast(" building mGoogleApiClient");
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        getLocation();



        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

        } else {


            showToast("Couldn't get the location. Make sure location is enabled on the device");
        }

//        txtview_termsconditions.setText("Terms & Conditions");





        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));



        txtview_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                 System.out.println("login clicked ");
                if (isConnectingToInternet() == true) {

                    String user_id = edittext_userID.getText().toString();
                    String password = edittext_password.getText().toString();
//                    try {
//
//                        password = convert_SHA1(password);
//
//                    } catch (Exception e) {
//
//                        e.printStackTrace();
//                    }
                    HashMap<String, String> login_params = new HashMap<String, String>();
                    System.out.println("username is " + user_id);
                    System.out.println("password is " + password);
//                    login_params.put("controller", "login");
//                    login_params.put("method", "login");
                    login_params.put("email", user_id);
                    login_params.put("password", password);
//                    login_params.put("latitude",latitude);
//                    login_params.put("latitude",latitude);
                    SharedPreferences sp =  getSharedPreferences("login_pref",0);

                    System.out.println("deviceID >> == <<"+ sp.getString("deviceID", "_"));
                    deviceID = sp.getString("deviceID", "_");
                    System.out.println("deviceID is " + deviceID);

                    if(mLastLocation != null){
                        latitude = mLastLocation.getLatitude();
                        longitude = mLastLocation.getLongitude();
                        LoginAsyncTask logintask = new LoginAsyncTask(LoginActivity.this,LoginActivity.this);
                        logintask.setLocation(latitude,longitude);
                        logintask.setDeviceId(deviceID);
                        logintask.execute(login_params);
//                        logintask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,login_params);
                    }
                    else
                    {
//                        showToast("Waiting for location");
                        getLocation();
                        if(mLastLocation != null) {
                            showToast("Got location");
                            latitude = mLastLocation.getLatitude();
                            longitude = mLastLocation.getLongitude();
                            LoginAsyncTask logintask = new LoginAsyncTask(LoginActivity.this, LoginActivity.this);
                            logintask.setLocation(latitude, longitude);
                            logintask.setDeviceId(deviceID);
                            logintask.execute(login_params);
                        }
                    }

//					JsonParser parser = new JsonParser();
//					System.out.println("login response "+login_response);
//					JsonParser.parse_login_response(login_response);
//					Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
//					startActivity(intent);
                } else {
                    final AlertDialog ad = new AlertDialog.Builder(LoginActivity.this).create();
                    ad.setTitle("No data connection !!!");
                    ad.setMessage("Please retry later");

                    ad.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            ad.dismiss();

                        }
                    });

                    // Showing Alert Message
                    ad.show();
                }

            }
        });

       /* txtview_forgotpassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);

            }
        });*/
        txtview_signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, UserRegistrationActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(setIntent);
        finish();
        System.exit(0);
        moveTaskToBack(true);


    }

  /* ---------------- location starts here ----------------- */


    /**
     * Creating google api client object
     * */

    protected synchronized void buildGoogleApiClient() {
//        showToast(" Connecting mGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .addConnectionCallbacks(LoginActivity.this)
                .addOnConnectionFailedListener(LoginActivity.this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10);



        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();
//                showToast("status of SettingsApi is "+status.getStatusMessage());

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }
    /* ----------  LocationListener ----------- */
    @Override
    public void onLocationChanged(Location location) {


//    showToast("Location changed updating location"+location.getLatitude() + "\n"+location.getLongitude());
//        Toast.makeText(LoginActivity.this, , Toast.LENGTH_SHORT).show();
//        mLastLocation = mGoogleApiClient.getLastLocation();
//        if(mLocationClient != null)
//            mLocationClient.requestLocationUpdates(mLocationRequest,  this);
    }

    /**
     * Google api callback methods
     */


    /* ----------   GoogleApiClient.ConnectionCallbacks ----------- */

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
//        showToast("connected.. Fetching Location");
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0)
    {
        showToast("connection suspended.. Connecting mGoogleApiClient");
        mGoogleApiClient.connect();
    }
    /* ----------   GoogleApiClient.OnConnectionFailedListener ----------- */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        showToast("Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    private void getLocation() {

        if (isPermissionGranted) {

            try
            {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if(mGoogleApiClient.isConnected())
                {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }
                else
                {
//                    showToast(" mGoogleApiClient is Disconnected");
                    mGoogleApiClient.connect();
                }

            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }

        }

    }

    private boolean checkPlayServices() {

//        showToast(" checking PlayServices");
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this,resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

        /* ---------------- location ends here ----------------- */





     /* ----------   for permissions ----------- */

    @Override
    public void PermissionGranted(int request_code) {
        Log.w("PERMISSION","GRANTED");
        isPermissionGranted=true;
        SensorService.isPermissionGranted=true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.w("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.w("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // redirects to utils

        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

}


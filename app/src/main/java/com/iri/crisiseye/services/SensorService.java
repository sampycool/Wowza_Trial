package com.iri.crisiseye.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.iri.crisiseye.LoginActivity;
import com.iri.crisiseye.asynctask.WebService_AsyncTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public int counter=0;
//    public  String lat ;
//    public  String lon ;
    String deviceID;
    String device_time ;
    public static String battery_level ;
    String orientation ;;
    HashMap<String, String> map;
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    public static boolean isPermissionGranted;
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;

//    LoginActivity loginActivity;
    Activity activity;
//    Location mLastLocation;

    double latitude;
    double longitude;


    int bat_level = 0;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }




    private BroadcastReceiver mBatInfoReceiver;

    /*public SensorService(Context applicationContext) {
        super();
        *//*this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));*//*
        System.out.println("HERE , here I am!");
    }*/

    public SensorService()
    {
        super();
    }
    public SensorService(Activity la) {
        super();
       /* mBatInfoReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent intent) {
                // TODO Auto-generated method stub
//            bat_level = intent.getIntExtra("bat_level", 0);


//            System.out.println("bat_level > "+ bat_level);

                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
                // Display the battery scale in TextView
                System.out.println("Battery Scale >> " + scale);

                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
                // Display the battery level in TextView
                System.out.println("Battery Level >> " + level);

                float percentage = level/ (float) scale;
                bat_level = (int)((percentage)*100);

                System.out.println("bat_level >> "+ bat_level);


            }
        };
        this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));*/
//        loginActivity = (LoginActivity) la;
        activity = la;
        System.out.println("THERE , here I am!");
    }


    private final IBinder binder = new SensorDataFeedBinder();

    public class SensorDataFeedBinder extends Binder
    {
        public SensorService getService()
        {
            System.out.println(">< called getService()");
            return SensorService.this;
        }


    }



    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println(">< In on start command ");
//         Intent intent = getIntent();

       longitude =  intent.getDoubleExtra("longitude",0.0);
       latitude = intent.getDoubleExtra("latitude",0.0);
//        map = (HashMap<String, String>) intent.getSerializableExtra("map");

        *//*String name = intent.getStringExtra("name");
        lat =  intent.getStringExtra("lat");
        lon =  intent.getStringExtra("lon");

        device_time = intent.getStringExtra("device_time");
        battery_level = intent.getStringExtra("battery_level");
        orientation = intent.getStringExtra("orientation");
        device_id = intent.getStringExtra("device_id");*//*


//
//        if (map == null) {
//
//            System.out.println("map is null");
//        }
//        else
//        {
//            System.out.println("map is not null");
//        }


//         lat = map.get("lat");
//         lon = map.get("lon");
//         device_time = map.get("device_time");
//         battery_level = map.get("battery_level");
//         orientation = map.get("orientation");
//         device_id = map.get("device_id");

       *//* System.out.println("lat > "+lat);
        System.out.println("lon > "+lon);
        System.out.println("device_time > "+device_time);
        System.out.println("battery_level > "+battery_level);
        System.out.println("orientation > "+orientation);
        System.out.println("device_id > "+device_id);*//*

        System.out.println(">< going to start timer from onStartCommand ");
        startTimer(latitude, longitude);


        return START_STICKY;
    }*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("EXIT , ondestroy!");
//        Intent broadcastIntent = new Intent("startedservices.trial.com.myapplication.ActivityRecognition.RestartSensor");
//        sendBroadcast(broadcastIntent);
        stoptimertask();
    }



    public void startTimer(double latitude, double longitude, String deviceID) {
        //set a new Timer
        timer = new Timer();

        if (checkPlayServices()) {
//            showToast("Fetching Location..");

//            showToast(" building mGoogleApiClient from service");
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        getLocation();



        if (mLastLocation != null) {
            this.latitude = mLastLocation.getLatitude();
            this.longitude = mLastLocation.getLongitude();

        } else {


//            showToast("Couldn't get the location from > Make sure location is enabled on the device");
            getLocation();
//            showToast("fetched loc > after failing to retreive locn..");
//            this.latitude = mLastLocation.getLatitude();
//            this.longitude = mLastLocation.getLongitude();
        }

        this.latitude = latitude;
        this.longitude = longitude;
//        this.latitude = mLastLocation.getLatitude();
//        this.longitude = mLastLocation.getLongitude();
        this.deviceID = deviceID;
        System.out.println(">< latitude in service :: "+latitude);
        System.out.println(">< longitude in service :: "+longitude);
        //initialize the TimerTask's job
        initializeTimerTask();

//      schedule the timer, to wake up every 1 second
        //delay 1000 / 0ms, repeat in 10000ms
        timer.schedule(timerTask, 0, 10000);
//        timer.schedule(timerTask, 1000);


    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        System.out.println(">< in initializeTimerTask");
        System.out.println("latitude >> "+latitude);
        System.out.println("longitude >> "+longitude);
        timerTask = new TimerTask() {
            public void run() {
//                System.out.println("counter >> "+ (counter++));

                try
                {

                if (getResources().getConfiguration().orientation == 1) {
                    System.out.println("orientation is portrait");
                    orientation = "portrait";
                } else if (getResources().getConfiguration().orientation == 2) {
                    System.out.println("orientation is landscape");
                    orientation = "landscape";
                }
//                getLocation();
                    mGoogleApiClient.connect();
                    if(mGoogleApiClient.isConnected()) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, SensorService.this, Looper.getMainLooper());
                    }

                SharedPreferences sp = getSharedPreferences("login_pref", 0);

                System.out.println("deviceID >> == <<" + sp.getString("deviceID", "_"));
                System.out.println("latitude >> == <<" + sp.getString("latitude", "0.0"));
                System.out.println("longitude >> == <<" + sp.getString("longitude", "0.0"));
                deviceID = sp.getString("deviceID", "_");
                latitude = Double.parseDouble(sp.getString("latitude", String.valueOf(latitude)));
                longitude = Double.parseDouble(sp.getString("longitude", String.valueOf(longitude)));

                System.out.println("lat -> " + latitude + "\n" + "lon -> " + longitude);

//                asdasdasd
//asdsd


                map = new HashMap<String, String>();

                map.put("lat", String.valueOf(latitude));
                map.put("lon", String.valueOf(longitude));

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                Date now = new Date();
                String strDate = sdf.format(now);
                map.put("device_time", strDate);
                map.put("battery_level", battery_level);
                map.put("orientation", orientation);

//                TelephonyManager tel_mangr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE) ;
//                device_id = tel_mangr.getDeviceId();
                map.put("device_id", deviceID);

                System.out.println("lat >> " + latitude);
                System.out.println("lon >> " + longitude);
                System.out.println("device_time >> " + strDate);
                System.out.println("battery_level >> " + battery_level);
                System.out.println("orientation >> " + orientation);
                System.out.println("deviceID >> " + deviceID);

                WebService_AsyncTask asyncTask = new WebService_AsyncTask(getApplicationContext());
                asyncTask.execute(map);
            }
                 catch (SecurityException e)
                {
                    e.printStackTrace();
                }

            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {

        System.out.println(">< in onBind ");
//        showToast("onBind");
//        showToast("Checking play services");
//        checkPlayServices();
        if (checkPlayServices()) {
//            showToast("Fetching Location..");

//            showToast(" building mGoogleApiClient from service");
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        mGoogleApiClient.connect();
        getLocation();
        return this.binder;

    }

    @Override
    public void onRebind(Intent intent) {
        System.out.println(">< in onRebind ");
//        showToast("onRebind");
//        showToast("Checking play services");
//        checkPlayServices();
        super.onRebind(intent);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println(">< in onUnbind ");
        mGoogleApiClient.disconnect();
        return true;
    }

    /* --------------- here in starts code for location services ------------------ */

   /* @Override
    public void onLocationChanged(Location location) {
        System.out.println("on location changed called >> ");
        mLastLocation = location;


        System.out.println("Setting marker>> ");
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
        System.out.println("latitude >< is "+ latitude);
        System.out.println("longitude >< is "+ longitude);
        *//*------- To get city name from coordinates -------- *//*
        String cityName = null;
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName;

        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }*/
   public void showToast(String message)
   {
       Toast.makeText(this,message+" >> service",Toast.LENGTH_SHORT).show();
   }
     /* ---------------- location starts here ----------------- */


    /**
     * Creating google api client object
     * */

    protected synchronized void buildGoogleApiClient() {
//        showToast(" Connecting mGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(SensorService.this)
                .addConnectionCallbacks(SensorService.this)
                .addOnConnectionFailedListener(SensorService.this)
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
                            status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);

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


//        showToast("Location changed updating location"+location.getLatitude() + "\n"+location.getLongitude());
        SharedPreferences pref = getSharedPreferences("login_pref",0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("latitude",String.valueOf(location.getLatitude()));
        editor.putString("longitude",String.valueOf(location.getLongitude()));
        editor.commit();
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
        mGoogleApiClient.connect();
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

        System.out.println(">> in getLocation()");
//        showToast(" isPermissionGranted " + isPermissionGranted);
        if (isPermissionGranted) {
//            showToast("in isPermissionGranted");

            try
            {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if(mGoogleApiClient.isConnected())
                {
                    System.out.println(">> mGoogleApiClient is connected");
//                    showToast(" >> mGoogleApiClient is Connected");
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }
                else
                {
                    System.out.println(">> mGoogleApiClient is disconnected");
//                    showToast(" >> mGoogleApiClient is Disconnected");
                    mGoogleApiClient.connect();
                }

                /*SharedPreferences pref = getSharedPreferences("login_pref",0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("latitude",String.valueOf(mLastLocation.getLatitude()));
                editor.putString("longitude",String.valueOf(mLastLocation.getLongitude()));
                editor.commit();*/
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
                googleApiAvailability.getErrorDialog(activity,resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
//                finish();
            }
            return false;
        }
        return true;
    }

        /* ---------------- location ends here ----------------- */
}

package com.iri.crisiseye;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.firebase.iid.FirebaseInstanceId;
import com.iri.crisiseye.services.SensorService;


public class MainActivity extends ListActivity  {

    private static final String TITLE = "example_title";
    private static final String DESCRIPTION = "example_description";
    private static final String ICON = "example_icon";
    private static final String CLASS_NAME = "class_name";

    Location mLastLocation;
//    private Button btn_test;

//    MyLocationListener locationListener;
//    private GoogleMap mMap;
//    GoogleApiClient mGoogleApiClient;
//    Location mLastLocation;
//    Marker mCurrLocationMarker;
//    Marker restMarker;
//    LocationRequest mLocationRequest;
//    double latitude;
//    double longitude;
    int bat_level;
    String orientation;
    HashMap<String,String> params;

    Intent mServiceIntent;
    private SensorService mSensorService;

    Toolbar toolbar;

    @Override
    protected void onStart() {
        super.onStart();
        /*if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }*/
    }

    @Override
    protected void onStop() {
//        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void onDestroy() {
       /* if (!isMyServiceRunning(mSensorService.getClass())) {
                     stopService(mServiceIntent);
                }*/

        if(mServiceIntent != null) {
            System.out.println("mService Intent not null");
            stopService(new Intent(this, mSensorService.getClass()));
            System.out.println();
        }
       /* if (mBatInfoReceiver != null) {
            unregisterReceiver(mBatInfoReceiver);
            mBatInfoReceiver = null;
        }*/
        super.onDestroy();
        finish();
        System.exit(0);
        moveTaskToBack(true);
    }

    private static final String[][] ACTIVITY_TEXT = {
            {"Stream live video and audio",
                    "Broadcast a live video and audio stream captured with the local camera and mic",
                    "CameraActivity"}

            /*{   "Capture an MP4 file",
                    "Broadcast a live video stream while saving it to an MP4 file",
                    "mp4.MP4CaptureActivity"            },

            {   "Stream an MP4 file",
                    "Broadcast video from an MP4 file stored on the local device",
                    "mp4.MP4BroadcastActivity"          },

            {   "Grab a screenshot",
                    "Take a snapshot of the camera preview",
                    "ScreenshotActivity"      },

            {   "Display a bitmap overlay",
                    "Display a bitmap as an overlay on the camera preview",
                    "graphics.BitmapOverlayActivity"    },

            {   "Display a text overlay",
                    "Display text as an overlay on the camera preview",
                    "graphics.TextOverlayActivity"      },

            {   "Facial recognition demo",
                    "Use facial recognition features to become a Wowza Ninja",
                    "FaceActivity"                      },

            {   "Display an audio level meter",
                    "Register an audio sample listener and display an audio level meter",
                    "audio.AudioMeterActivity"          },

            {   "Use a Bluetooth mic for audio capture",
                    "Use a Bluetooth mic for streaming audio if present",
                    "audio.BluetoothActivity"          },

            {   "Display detailed device information",
                    "Demonstrates the informational APIs available in the SDK",
                    "InfoActivity"                      }*/

    };

    private static final int[] ACTIVITY_ICONS = {
            com.iri.crisiseye.R.drawable.ic_streaming,
            /*com.trial.cvdi.R.drawable.ic_mp4_capture,
            com.trial.cvdi.R.drawable.ic_mp4_streaming,
            com.trial.cvdi.R.drawable.ic_take_screenshot,
            com.trial.cvdi.R.drawable.ic_bitmap,
            com.trial.cvdi.R.drawable.ic_text_overlay,
            com.trial.cvdi.R.drawable.ic_face,
            com.trial.cvdi.R.drawable.ic_audio_meter,
            com.trial.cvdi.R.drawable.ic_bluetooth,
            com.trial.cvdi.R.drawable.ic_info*/
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.iri.crisiseye.R.layout.activity_main);

//        if (getActionBar() != null) {
//            getActionBar().setTitle(getResources().getString(com.iri.crisiseye.R.string.app_name_long));
//        }



        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setActionBar(toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText("CrisisEye");

        System.out.println("hello 1 >> ");


//        this.registerReceiver(this.mBatInfoReceiver,
//                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

//        mGoogleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();

//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);


        setListAdapter(new SimpleAdapter(this, createActivityList(),
                com.iri.crisiseye.R.layout.example_row,
                new String[]{TITLE, DESCRIPTION, ICON},
                new int[]{com.iri.crisiseye.R.id.example_title, com.iri.crisiseye.R.id.example_description, com.iri.crisiseye.R.id.example_icon}));


//        btn_test = (Button) findViewById(R.id.btn_test);
//        System.out.println("hello 5 >> ");
////        final LocationManager finalLocationManager = locationManager;
//        btn_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                /*System.out.println("web service called");
//                WebService_AsyncTask asyncTask = new WebService_AsyncTask();
//                asyncTask.execute();*/
//                System.out.println("button clicked  is >> ");
//
////                locationListener = new MyLocationListener(MainActivity.this);
//
//                /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }*/
//
//
////                LocationManager.requestLocationUpdates(LocationManager
////                        .GPS_PROVIDER, 5000, 10, locationListener);
//
//
//
////                System.out.println("latitude is >> "+ locationListener.getLatitude());
////                System.out.println("longitude is  >> "+locationListener.getLongitude());
//
//                /*mSensorService = new SensorService(MainActivity.this);
//                mServiceIntent = new Intent(MainActivity.this, mSensorService.getClass());
////                mServiceIntent.putExtra("map",  params);
//                mServiceIntent.putExtra("name",  "Priyam");
//                mServiceIntent.putExtra("lat", Double.toString(latitude));
//                mServiceIntent.putExtra("lon", Double.toString(longitude));
//                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
//                Date now = new Date();
//                String strDate = sdf.format(now);
//                mServiceIntent.putExtra("device_time", strDate);
//                mServiceIntent.putExtra("battery_level",Integer.toString(bat_level));
//                mServiceIntent.putExtra("orientation",orientation);
//                mServiceIntent.putExtra("device_id","Device1 Samsung Galaxy S6");
//                startService(mServiceIntent);*/
//
//
//                /*if (!isMyServiceRunning(mSensorService.getClass())) {
//                    startService(mServiceIntent);
//                }*/
//
//            }
//        });
        System.out.println("hello 6 >> ");


    }



    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Map<String, Object> map = (Map<String, Object>)listView.getItemAtPosition(position);

        Iterator itr = map.entrySet().iterator();

        /*while (itr.hasNext()) {
            Map.Entry pairs = (Map.Entry) itr.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());


//            itr.remove(); // avoids a ConcurrentModificationException
        }*/

//        mSensorService = new SensorService(MainActivity.this);
        mSensorService = new SensorService(MainActivity.this);
        mServiceIntent = new Intent(MainActivity.this, mSensorService.getClass());
//                mServiceIntent.putExtra("map",  params);

//        mServiceIntent.putExtra("lat", Double.toString(latitude));
//        mServiceIntent.putExtra("lon", Double.toString(longitude));
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date now = new Date();
        String strDate = sdf.format(now);
        mServiceIntent.putExtra("device_time", strDate);
        mServiceIntent.putExtra("battery_level",Integer.toString(bat_level));
        mServiceIntent.putExtra("orientation",orientation);
        mServiceIntent.putExtra("device_id","Nexus 6");
//        startService(mServiceIntent);

        String tkn = FirebaseInstanceId.getInstance().getToken();
//        Toast.makeText(MainActivity.this, "Current token ["+tkn+"]",
//                Toast.LENGTH_LONG).show();


        Log.v("App", "Token ["+tkn+"]");

        Intent intent = (Intent) map.get(CLASS_NAME);

        startActivity(intent);
    }

    private List<Map<String, Object>> createActivityList() {
        List<Map<String, Object>> activityList = new ArrayList<Map<String, Object>>();

        for (int i=0;i<ACTIVITY_TEXT.length; i++) {
            String activity_text[] = ACTIVITY_TEXT[i];
            int activity_icon = ACTIVITY_ICONS[i];

            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put(TITLE, activity_text[0]);
            tmp.put(DESCRIPTION, activity_text[1]);
            tmp.put(ICON, activity_icon);

            System.out.println("activity to be started is = " + activity_text[2]);
            Intent intent = new Intent();
            try {
                Class cls = Class.forName("com.iri.crisiseye." + activity_text[2]);
                intent.setClass(this, cls);
                tmp.put(CLASS_NAME, intent);
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("Unable to find >> " + activity_text[2], cnfe);
            }

            activityList.add(tmp);
        }

        return activityList;
    }

    /*@Override
    public void onConnected(@Nullable Bundle bundle) {
//        Log.i(MainActivity.class.getSimpleName(), "Connected to Google Play Services!");
        System.out.println("Connected to Google Play Services!");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
//            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

//            double lat = lastLocation.getLatitude();
//            double lon = lastLocation.getLongitude();
//            System.out.println("latitude >> "+ lat);
//            System.out.println("longitude >> "+ lon);
//            latitude = lat;
//            longitude = lon;

            params = new HashMap<String,String>();

//            System.out.println("query is >> "+ exact_locn+" "+city+" "+state);
//            params.put("query", exact_locn+" "+city+" "+state);
//            params.put("lat", Double.toString(latitude));
//            params.put("lon", Double.toString(longitude));
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            Date now = new Date();
            String strDate = sdf.format(now);
            params.put("device_time", strDate);
            params.put("battery_level",Integer.toString(bat_level));
            params.put("orientation",orientation);
            params.put("device_id","Device1 Samsung Galaxy S6");



//            WebService_AsyncTask asyncTask = new WebService_AsyncTask(MainActivity.this);
//            asyncTask.execute(params);

//            fetch_resulttaask.delegate = MainActivity.this;

//            String units = "imperial";
//            String url = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=%s&appid=%s",
//                    lat, lon, units, APP_ID);
//            WebService_AsyncTask asyncTask = new WebService_AsyncTask();
//            asyncTask.execute();
        }
    }*/




    /*@Override
    public void onLocationChanged(Location location) {

        System.out.println("on location changed called >> ");
        mLastLocation = location;
        Toast.makeText(
                getBaseContext(),
                "Location changed: Lat: " + location.getLatitude() + " Lng: "
                        + location.getLongitude(), Toast.LENGTH_SHORT).show();


        System.out.println("Setting marker>> ");
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
        System.out.println("latitude > is "+ latitude);
        System.out.println("longitude > is "+ longitude);
        *//*------- To get city name from coordinates -------- *//*
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                System.out.println("isMyServiceRunning true +");
                return true;
            }
        }
        System.out.println("isMyServiceRunning false +");
        return false;
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);

        ad.setTitle("Exit App");
        ad.setMessage("Are you sure to exit?");
        ad.setPositiveButton("Exit",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
//                IntentFilter filter = new IntentFilter();
//                filter.addAction("abc");
//                SensorRestarterBroadcastReceiver mBroadcastListener = new SensorRestarterBroadcastReceiver();
//                registerReceiver(mBroadcastListener, filter);
                Intent intent_service = new Intent("abc");
                sendBroadcast(intent_service);
                finish();
//                System.exit(0);
//                moveTaskToBack(true);
               /* Intent setIntent = new Intent(Intent.ACTION_MAIN);
                setIntent.addCategory(Intent.CATEGORY_HOME);
                startActivity(setIntent);
                finish();
                System.exit(0);
                moveTaskToBack(true);*/
            }
        });
        ad.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Cancel");
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = ad.create();
        alertDialog.show();
    }

    /**
     * This is for battery level detection
     *
     *
     *
     * */


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            bat_level = intent.getIntExtra("bat_level", 0);


            System.out.println("bat_level > "+ bat_level);

            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
            // Display the battery scale in TextView
            System.out.println("Battery Scale >> " + scale);

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            // Display the battery level in TextView
            System.out.println("Battery Level >> " + level);

            float percentage = level/ (float) scale;
            bat_level = (int)((percentage)*100);

            System.out.println("bat_level >> "+ bat_level);
            if(getResources().getConfiguration().orientation ==1)
            {
                System.out.println("orientation is portrait");
                orientation = "portrait";
            }
            else if(getResources().getConfiguration().orientation ==2)
            {
                System.out.println("orientation is landscape");
                orientation = "landscape";
            }

        }
    };


}

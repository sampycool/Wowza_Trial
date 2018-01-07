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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.iri.crisiseye.asynctask.LoginAsyncTask;
import com.iri.crisiseye.asynctask.PermissionResultCallback;
import com.iri.crisiseye.services.SensorService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//import android.location.LocationListener;

//import static com.iri.crisiseye.services.SensorService.battery_level;


public class LoginActivity_Temp extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback,PermissionResultCallback {

    TextView txtview_termsconditions;
//    TextView txtview_forgotpassword;
//    Location mLastLocation;

    GoogleApiClient gac;
    LocationRequest locRequest;
    double latitude  ;
    double longitude ;
    String deviceID = "";
    TextView txtview_signup;
    TextView txtview_login;
    EditText edittext_userID;
    EditText edittext_password;
    String login_response;
    Toolbar toolbar;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    long FASTEST_INTERVAL = 2000; // 2 seconds
    long UPDATE_INTERVAL =  2*1000; // 2 seconds

    ArrayList<String> permissions=new ArrayList<>();

    PermissionUtils permissionUtils;

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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // redirects to utils

        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }


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

    @Override
    protected void onStart() {
        gac.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        gac.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        permissionUtils=new PermissionUtils(LoginActivity_Temp.this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.READ_PHONE_STATE);

        permissionUtils.check_permission(permissions,"Explain here why the app needs permissions",1);

        gac = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).
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

//        txtview_termsconditions.setText("Terms & Conditions");

        /* ---------------- location starts here ----------------- */
        int resultcode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        System.out.println(">< resultcode is :: " + resultcode);
//        Toast.makeText(this, "Google Play resultcode is ! "+ resultcode, Toast.LENGTH_SHORT).show();

        switch(resultcode)
        {
            case ConnectionResult.SUCCESS:
                System.out.println(">< in ConnectionResult.SUCCESS :: " + resultcode);
//                Toast.makeText(this, "Google Play services API good to go!", Toast.LENGTH_SHORT).show();
                break;
            default:
                showPlayServicesError(resultcode);
                return;
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }


        LocationManager lm_service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = lm_service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = lm_service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        System.out.println(">< lm_service is "+ gps_enabled +" :: " +network_enabled);
        if((network_enabled == false) &&  (gps_enabled == false)){
            System.out.println("in if condition of lm_service");
            System.out.println(">< lm_service is "+ gps_enabled +" :: " +network_enabled);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Enable Location").setMessage("Your location settings is turned off \n" +
            "Please turn on your location settings").setPositiveButton("Location Settings",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();

        }

        locRequest = new LocationRequest();
        locRequest.setFastestInterval(FASTEST_INTERVAL);
        locRequest.setInterval(UPDATE_INTERVAL);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);





        /* ---------------- location ends here ----------------- */



        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));



        txtview_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // System.out.println("host_validation_response is
                // "+host_validation_response);
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

                    /*
                    LoginAsyncTask logintask = new LoginAsyncTask(this);
                    logintask.setLocation(latitude,longitude);
                    logintask.setDeviceId(deviceID);
                    logintask.execute(login_params);
                    */

//					JsonParser parser = new JsonParser();
//					System.out.println("login response "+login_response);
//					JsonParser.parse_login_response(login_response);
//					Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
//					startActivity(intent);
                } else {
                    final AlertDialog ad = new AlertDialog.Builder(LoginActivity_Temp.this).create();
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
                Intent intent = new Intent(LoginActivity_Temp.this, UserRegistrationActivity.class);
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

        /*Exception_1 = java.lang.ClassNotFoundException: Didn't find class "com.qualcomm.qti.Performance" on path: DexPathList[[],nativeLibraryDirectories=[/system/lib64, /vendor/lib64]]
        12-28 22:40:46.127 2478-3558/com.iri.crisiseye E/UncaughtException: java.lang.SecurityException: getDeviceId: Neither user 10228 nor current process has android.permission.READ_PHONE_STATE.
                at android.os.Parcel.readException(Parcel.java:1693)
        at android.os.Parcel.readException(Parcel.java:1646)
        at com.android.internal.telephony.ITelephony$Stub$Proxy.getDeviceId(ITelephony.java:5139)
        at android.telephony.TelephonyManager.getDeviceId(TelephonyManager.java:914)
        at com.iri.crisiseye.services.SensorService$1.run(SensorService.java:241)
        at java.util.TimerThread.mainLoop(Timer.java:555)
        at java.util.TimerThread.run(Timer.java:505)*/
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("on location changed called >> ");
//        mLastLocation = location;

        if(location != null)
        {
            System.out.println("location not null ><");
            latitude  = location.getLatitude();
            longitude = location.getLongitude();
            System.out.println("latitude >< is "+ latitude);
            System.out.println("longitude >< is "+ longitude);
//            SensorService ss = new SensorService();
//            ss.setLatitude(latitude);
//            ss.setLongitude(longitude);
//            SensorService.lat = String.valueOf(latitude);
//            SensorService.lon = String.valueOf(longitude);
        }
        else if(location == null )
        {
            System.out.println("location is null");
        }
        /*------- To get city name from coordinates -------- */
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
        System.out.println(" lat long details are >< "+s);

//        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
    }

    /*@Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
*/

    /* ----------   GoogleApiClient.ConnectionCallbacks ----------- */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }

        Location ll = LocationServices.FusedLocationApi.getLastLocation(gac);
        if( ll == null)
        {
            System.out.println("No last location");
        }
        else if( ll != null)
        {
            System.out.println(" >< ll >> "+ll.toString());
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(gac, locRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    /* ----------   GoogleApiClient.OnConnectionFailedListener ----------- */

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, "Google api connection failed :\n", Toast.LENGTH_SHORT).show();

    }



     /* ----------   for permissions ----------- */

    @Override
    public void PermissionGranted(int request_code) {
        Log.w("PERMISSION","GRANTED");
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

}


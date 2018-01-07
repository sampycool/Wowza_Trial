package com.iri.crisiseye.asynctask;

/**
 * Created by tsarkar on 02/10/17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.iri.crisiseye.LoginActivity;
import com.iri.crisiseye.MainActivity;
import com.iri.crisiseye.services.SensorRestarterBroadcastReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



/**
 * Created by tsarkar on 07/08/17.
 */


public class LoginAsyncTask  extends AsyncTask<HashMap<String,String>, Void, String> {

    //    String fetchparkingSpotsURL = "http://10.231.243.14:8080/test_jsp/parking/webservice/parkinglocations  http://tsarkarwebservice.ddns.net/test_jsp/parking/webservice/parkinglocations";
    //    String fetchparkingSpotsURL = "http://192.168.43.64:8080/test_jsp/parking/webservice/parkinglocations";
//    http://10.231.243.14:8080/test_jsp/parking/webservice/parkinglocations
    private Context mContext;
    ProgressDialog mProgress;
    LoginActivity loginActivity;
    SensorRestarterBroadcastReceiver mBroadcastListener;
//    public AsyncResponse1 delegate = null;
    InputStream in = null;
    String weather_url = "";//AppConstants.login_url;
    String email = "";
    String deviceID = "";
    double latitude;
    double longitude;
    public LoginAsyncTask(Context context, LoginActivity la) {
        this.mContext = context;
        this.loginActivity = la;
        System.out.println("in constructor login asynctask");
    }

    public void setDeviceId(String deviceID) {
        this.deviceID = deviceID;
        System.out.println("deviceID is >> " + deviceID);
    }
    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude =longitude;
    }
    @Override
    protected String doInBackground(HashMap<String, String>... params) {
//        System.out.println("fetch parking spots url is " + fetchparkingSpotsURL);
        String response = "";
        try {
//            String webserviceURL = "http://tsarkarwebservice.ddns.net/RestfulWebServiceCA/contextaware/webservice/ccstationlocations";
//            String webserviceURL = "http://tsarkarwebservice.ddns.net/test_jsp/parking/webservice/parkinglocations";
//            String webserviceURL =  "http://10.231.243.14:8080/test_jsp/parking/webservice/parkinglocations";emergencyroom.ddns.net

            String params_formed = getQuery(params[0]);
            byte[] postData  = params_formed.getBytes();
            int postDataLength = postData.length;


//            String webserviceURL = "http://emergencyroom.ddns.net:8080/Er_App_RestServices/erapp/webservice/login";
            String webserviceURL = "http://192.168.1.22:8080/CrisisEyeWebServices/crisiseye/webservice/login";
//            String webserviceURL = "http://192.168.10.200:8080/CrisisEyeWebServices/crisiseye/webservice/login";
//            String webserviceURL = "http://192.168.201.53:8080/CrisisEyeWebServices/crisiseye/webservice/login";
            //        192.168.43.198  //192.168.1.106  140609
            //http://localhost:8080/CrisisEyeWebServices/crisiseye/webservice/login?email=raj@gmail.com&password=r22





            InputStream in = null;
            String result = "";
            System.out.println("++ In new connection function");

            URL url = new URL(webserviceURL+"?"+params_formed);
            System.out.println("++ hello 1");
            URLConnection connection = url.openConnection();
            System.out.println("++ hello 2");
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            System.out.println("++ hello 3");
            System.out.println("++ url is " + connection.getURL());


            System.out.println("++ hello 3");
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            //            httpConnection.setDoOutput(true);
            System.out.println("++ hello 4");
            //            httpConnection.setInstanceFollowRedirects( false );
            System.out.println("++ hello 5");
//            connection.setDoInput(true);
//            httpConnection.setDoOutput(true);
            System.out.println("++ hello 6");
            //            httpConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            System.out.println("++ hello 7");
            //            httpConnection.setRequestProperty( "charset", "utf-8");
            System.out.println("++ hello 8");
            //  			httpConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));

            httpConnection.setUseCaches(false);

            System.out.println("++ hello 9");
//            System.out.println("++ final url is " + connection.getURL()+params_formed);


//            httpConnection.setRequestProperty("Content-Type", "application/plain");
            //			httpConnection.setRequestProperty ("user-key", new String(URLEncoder.encode("a472f0195e685a4eeae803aef02d0d4a")));
            //                httpConnection.setRequestProperty ("user-key", "a472f0195e685a4eeae803aef02d0d4a");
//            httpConnection.setRequestProperty("Content-Language", "en-US");
            //                httpConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            //		    httpConnection.setConnectTimeout(5000);


            in = httpConnection.getInputStream();
            System.out.println("++ hello 8");
            int responseCode = httpConnection.getResponseCode();

            System.out.println("++ hello 9");
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("++ respone code is ok");
                StringBuilder sb = new StringBuilder("");
                // int line = 0;
                int length = httpConnection.getContentLength();
                System.out.println("++ Length = " + length);

                int i;
                do {
                    i = in.read();
                    if (i != -1) {
                        // System.out.println("Character >>> "+ (char)i);
                        sb.append((char) i);
                    }
                } while (i != -1);

                response = new String(sb);
                in.close();
                // result = sb.toString();
                System.out.println("++ response is !!!" + response);
                // return result;
            }
        } catch (Exception e) {

            e.printStackTrace();
            response = "failure";

            System.out.println("++ Exception e is " + e);
            //timeout = true;

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Final response....." + response);
        return response;
    }

    private String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        SharedPreferences pref = mContext.getSharedPreferences("login_pref", 0);
        String token = pref.getString("token", "token");
        params.put("token", token);
        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            if(pairs.getKey().toString().trim().equals("email"))
            {
                email = pairs.getValue().toString();
                System.out.println("pairs getkey >< email is :: "+ email);
            }

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode((String) pairs.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pairs.getValue(), "UTF-8"));
            it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("result in url parameter query formation is " + result);
        return result.toString();
    }




    @Override
    protected void onPreExecute() {
        System.out.println("in on preexecute");
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage("Signing In");
        mProgress.setCancelable(true);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }

    @Override
    protected void onPostExecute(String response) {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
            mProgress = null;
        }
        System.out.println("+ response is " + response);

        if (response == null) {
            Toast.makeText(mContext, "Sorry some error occurred",
                    Toast.LENGTH_LONG).show();
        } else {
            System.out.println("++ response is " + response);
            System.out.println("response length is  " + response.length());
//            JsonParser json_parser = new JsonParser();
//            HashMap parking_detls = json_parser.parse_parkingresults(response);
//            int size = parking_detls.size();
//            System.out.println("size of returned hashmap is >> "+ size);
//            delegate.processFinish(parking_detls);
            if (response.trim().equals("success")) {
                SharedPreferences pref = mContext.getSharedPreferences("login_pref",0);
                SharedPreferences.Editor editor = pref.edit();
                System.out.println("email is  :: " + email);
                editor.putString("email",email);
                editor.commit();
                Intent intent_service = new Intent("xyz");
                intent_service.putExtra("latitude",latitude);
                intent_service.putExtra("longitude",longitude);
                System.out.println("deviceID is >> " + deviceID);
                intent_service.putExtra("deviceID",deviceID);
//                mContext.sendBroadcast(intent_service);
                IntentFilter filter = new IntentFilter();
                filter.addAction("xyz");
                filter.addAction("abc");
//                filter.addAction(Intent.ACTION_BATTERY_CHANGED);
                mBroadcastListener = new SensorRestarterBroadcastReceiver();
                mBroadcastListener.setActivity(loginActivity);
                mContext.registerReceiver(mBroadcastListener, filter);

                mContext.sendBroadcast(intent_service);
                Intent login_activity = new Intent(mContext, MainActivity.class);
//                mContext.unregisterReceiver(mBroadcastListener);
                mContext.startActivity(login_activity);

            }


            else if (response.equals("failure")) {
                Toast.makeText(mContext, "Sorry username/password is invalid",
                        Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(mContext, response.trim(),
                        Toast.LENGTH_LONG).show();
            }



        }
    }


}

